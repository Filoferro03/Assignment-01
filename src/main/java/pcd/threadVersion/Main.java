package pcd.threadVersion;

import pcd.threadVersion.controller.*;
import pcd.threadVersion.model.*;
import pcd.threadVersion.util.BoundedBufferImpl;
import pcd.threadVersion.util.CustomCyclicBarrier;
import pcd.threadVersion.util.MasterWorkerMonitor;
import pcd.threadVersion.util.PhysicsWorker;
import pcd.threadVersion.view.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        //BoardConf conf = new MassiveBoardConf();
        BoardConf conf = new LargeBoardConf();

        Board board = new Board(conf);

        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 900, 700, buffer);

        int numWorkers = Runtime.getRuntime().availableProcessors() - 1;
        MasterWorkerMonitor masterMonitor = new MasterWorkerMonitor(numWorkers);
        CustomCyclicBarrier internalBarrier = new CustomCyclicBarrier(numWorkers, board::buildSpatialGrid);
        List<PhysicsWorker> workers = new ArrayList<>();
        for (int i = 0; i < numWorkers; i++) {
            PhysicsWorker worker = new PhysicsWorker(board, i, numWorkers, masterMonitor, internalBarrier);
            workers.add(worker);
            worker.start();
        }
        ActiveController gameLoop = new ActiveController(board, view, viewModel, buffer, masterMonitor, workers);

        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();

        gameLoop.start();
    }
}
