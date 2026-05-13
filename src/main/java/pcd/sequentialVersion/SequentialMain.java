package pcd.sequentialVersion;

import pcd.common.controller.BotPlayer;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.model.BoardConf;
import pcd.common.model.MassiveBoardConf;
import pcd.common.util.BoundedBufferImpl;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

public class SequentialMain {
    static void main() {
        BoardConf conf = new MassiveBoardConf();
        //BoardConf conf = new LargeBoardConf();
        Board board = new Board(conf);

        BoundedBufferImpl<Cmd> buffer = new BoundedBufferImpl<>(10);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 900, 700, buffer);

        SequentialController gameLoop = new SequentialController(board, view, viewModel, buffer);

        BotPlayer bot = new BotPlayer(buffer, board);
        bot.start();
        gameLoop.start();
    }
}