package chess;

import java.util.ArrayList;

public class Knight extends Piece{
    //constructor
    public Knight (PieceType pieceType, PieceFile pieceFile, int pieceRank, String color){
        super(pieceType, pieceFile, pieceRank, color);
    }


    public boolean canMove(String fileRankfinal){

        //create arraylist to store the possible moves of knight
        ArrayList<String> possibleMoves = new ArrayList<String>();
        //for loop to append possible moves of knight in the positive direction(up) and negative direction(down) line by line
        for(int i = 0; i < 2; i++){
            //by using -96 converts file to int with 1 = 'a' ... 8 = "h"
            //-i and +i help deal with file incrementation and decrementation based of how many rows we have moved up
            //-2 and +2 deal with file scoping 
            int currentFileMin = (this.pieceFile.toString().charAt(0) - 96) + i - 2;
            int currentFileMax = (this.pieceFile.toString().charAt(0) - 96) - i + 2;
            //imeediately goes to rank above the current knight 
            int currentRankUp = this.pieceRank + (i+1);
            int currentRankDown = this.pieceRank - (i+1);
            //makes sure of no rank out of bound
            if(currentRankUp < 9){
                //makes sure no file max out of bounds
                if(currentFileMax < 9){
                    String topMax = Character.toString(currentFileMax + 96) + currentRankUp;
                    //ensures that spot is either open or of opposite color
                    if(Chess.openSquare(topMax) || ((Chess.pieceAt(topMax) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(topMax)).getColor()))){
                        possibleMoves.add(topMax);
                    }
                }
                //makes sure no file min out of bounds
                if(currentFileMin > 0){
                    String topMin = Character.toString(currentFileMin + 96) + currentRankUp;
                    //ensures that spot is either open or of opposite color
                    if(Chess.openSquare(topMin) || ((Chess.pieceAt(topMin) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(topMin)).getColor()))){
                        possibleMoves.add(topMin);
                    }
                }
            }
            if(currentRankDown > 0){
                //makes sure no file max out of bounds
                if(currentFileMax < 9){
                    String bottomMax = Character.toString(currentFileMax + 96) + currentRankDown;
                    //ensures that spot is either open or of opposite color
                    if(Chess.openSquare(bottomMax) || ((Chess.pieceAt(bottomMax) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(bottomMax)).getColor()))){
                        possibleMoves.add(bottomMax);
                    }                
                }
                //makes sure no file min out of bounds
                if(currentFileMin > 0){
                    String bottomMin = Character.toString(currentFileMin + 96) + currentRankDown;
                    //ensures that spot is either open or of opposite color
                    if(Chess.openSquare(bottomMin) || ((Chess.pieceAt(bottomMin) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(bottomMin)).getColor()))){
                        possibleMoves.add(bottomMin);
                    }
                }
            }
        }
        //if fileRankfinal is contained in the possible moves arraylist then return true
        if(possibleMoves.contains(fileRankfinal)){
            return true;
        }
        return false;
    }
}
