package pcd.threadVersion;

import pcd.common.controller.BotPlayer;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.model.BoardConf;
import pcd.common.model.MassiveBoardConf;
import pcd.common.view.View;
import pcd.common.view.ViewModel;
import pcd.common.util.BoundedBufferImpl;

public class ThreadMain {
    public static void main(String[] args) {
        BoardConf conf = new MassiveBoardConf();
        //BoardConf conf = new LargeBoardConf();
        Board board = new Board(conf);
        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(100);
        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 900, 700, buffer);
        ThreadController gameLoop = new ThreadController(board, view, viewModel, buffer);
        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();
        gameLoop.start();
    }
}
