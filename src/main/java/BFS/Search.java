package BFS;

import gui.PuzzleApplication;
import javafx.application.Application;
import model.Position;
import model.TowerPuzzleModel;
import puzzle.TwoPhaseMoveState;
import puzzle.solver.BreadthFirstSearch;

public class Search {
    public static void main(String[] args) {


        new BreadthFirstSearch<TwoPhaseMoveState.TwoPhaseMove<Position>>().solveAndPrintSolution(new TowerPuzzleModel());


    }
}
