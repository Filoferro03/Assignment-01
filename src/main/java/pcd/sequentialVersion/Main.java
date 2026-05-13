package pcd.sequentialVersion;

import pcd.taskVersion.controller.ActiveController;
import pcd.taskVersion.controller.BotPlayer;
import pcd.taskVersion.controller.Cmd;
import pcd.taskVersion.model.Board;
import pcd.taskVersion.model.BoardConf;
import pcd.taskVersion.model.MassiveBoardConf;
import pcd.taskVersion.util.BoundedBufferImpl;
import pcd.taskVersion.view.View;
import pcd.taskVersion.view.ViewModel;

public class Main {
    static void main() {
        BoardConf conf = new MassiveBoardConf();
        //BoardConf conf = new LargeBoardConf();
        pcd.taskVersion.model.Board board = new Board(conf);

        pcd.taskVersion.util.BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        pcd.taskVersion.view.ViewModel viewModel = new ViewModel();
        pcd.taskVersion.view.View view = new View(viewModel, 900, 700, buffer);

        pcd.taskVersion.controller.ActiveController gameLoop = new ActiveController(board, view, viewModel, buffer);

        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();
        gameLoop.start();
    }
}