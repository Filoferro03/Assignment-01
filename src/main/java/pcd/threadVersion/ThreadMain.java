package pcd.threadVersion;

import pcd.common.controller.BotPlayer;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.model.BoardConf;
import pcd.common.model.MassiveBoardConf;
import pcd.common.view.View;
import pcd.common.view.ViewModel;
import pcd.common.util.BoundedBufferImpl;

import java.util.ArrayList;
import java.util.List;

public class ThreadMain {
    static void main() {
        BoardConf conf = new MassiveBoardConf();
        //BoardConf conf = new LargeBoardConf();

        Board board = new Board(conf);

        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 900, 700, buffer);

        int numWorkers = Runtime.getRuntime().availableProcessors() + 1;
        MasterWorkerMonitor masterMonitor = new MasterWorkerMonitor(numWorkers);
        List<PhysicsWorker> workers = new ArrayList<>();
        for (int i = 0; i < numWorkers; i++) {
            PhysicsWorker worker = new PhysicsWorker(board, i, numWorkers, masterMonitor);
            workers.add(worker);
            worker.start();
        }
        ThreadController gameLoop = new ThreadController(board, view, viewModel, buffer, masterMonitor, workers);

        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();

        gameLoop.start();
    }
}
