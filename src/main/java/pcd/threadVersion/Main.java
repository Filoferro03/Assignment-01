package pcd.threadVersion;

import pcd.threadVersion.controller.*;
import pcd.threadVersion.model.*;
import pcd.threadVersion.util.BoundedBufferImpl;
import pcd.threadVersion.view.*;

public class Main {
    public static void main(String[] args) {
        BoardConf conf = new LargeBoardConf();

        Board board = new Board();
        board.init(conf);

        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 600, 600, buffer);

        ActiveController gameLoop = new ActiveController(board, view, viewModel, buffer);

        BotPlayer bot = new BotPlayer(buffer);
        bot.start();

        gameLoop.start();
    }
}
