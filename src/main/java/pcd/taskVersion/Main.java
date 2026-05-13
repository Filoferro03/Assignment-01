package pcd.taskVersion;

import pcd.taskVersion.controller.ActiveController;
import pcd.taskVersion.controller.BotPlayer;
import pcd.taskVersion.controller.Cmd;
import pcd.taskVersion.model.*;
import pcd.taskVersion.util.BoundedBufferImpl;
import pcd.taskVersion.view.View;
import pcd.taskVersion.view.ViewModel;


public class Main {
    static void main() {
        BoardConf conf = new MassiveBoardConf();
        //BoardConf conf = new LargeBoardConf();
        Board board = new Board(conf);

        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 900, 700, buffer);

        ActiveController gameLoop = new ActiveController(board, view, viewModel, buffer);

        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();
        gameLoop.start();
    }
}
