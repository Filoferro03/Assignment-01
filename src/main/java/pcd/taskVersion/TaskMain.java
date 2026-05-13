package pcd.taskVersion;

import pcd.common.controller.BotPlayer;
import pcd.common.controller.Cmd;
import pcd.common.model.*;
import pcd.common.util.BoundedBufferImpl;
import pcd.common.view.View;
import pcd.common.view.ViewModel;


public class TaskMain {
    static void main() {
        BoardConf conf = new MassiveBoardConf();
        //BoardConf conf = new LargeBoardConf();
        Board board = new Board(conf);

        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 900, 700, buffer);

        TaskController gameLoop = new TaskController(board, view, viewModel, buffer);

        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();
        gameLoop.start();
    }
}
