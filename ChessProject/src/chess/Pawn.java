package chess;

public class Pawn extends Piece{
    //pown can promote
    private boolean firstMove;
    private int MOVE_MAX =1;
    
    //constructor
    public Pawn (PieceType pieceType, PieceFile pieceFile, int pieceRank, String color){
        super(pieceType, pieceFile, pieceRank, color);
        firstMove = true;
    }

    public boolean canMove(String fileRankFinal){
        //check for color
        //if white must be increasing and black must be descreasing 
        switch(this.pieceType){
            case WP:
                if(Chess.positionRank(fileRankFinal)<this.pieceRank) return false;
                break;
            case BP:
                MOVE_MAX =-1;//to go down
                if(Chess.positionRank(fileRankFinal)>this.pieceRank) return false;
                break;
            default:
        }

        //handle normal move
        if(Chess.openSquare(fileRankFinal) && Chess.positionRank(fileRankFinal)==this.pieceRank+MOVE_MAX 
            && ((this.pieceFile).name()).equals(Chess.positionFile(fileRankFinal))){
                return true;
        }

        //firstMove 
        if(Chess.openSquare(fileRankFinal) && firstMove && Chess.positionRank(fileRankFinal)==this.pieceRank+(MOVE_MAX*2) 
            && ((this.pieceFile).name()).equals(Chess.positionFile(fileRankFinal))){
            firstMove = false;
            return canMove(Chess.positionFile(fileRankFinal)+(Chess.positionRank(fileRankFinal)-(MOVE_MAX)));
        }

        //capture move
        //need to check for diagonals
        if(!(Chess.openSquare(fileRankFinal)) && //so if there is a piece there, so it can be captured
            (Math.abs(this.pieceRank-Chess.positionRank(fileRankFinal))==1) //the max horizontal distance is 1
            && Math.abs(((this.pieceFile).name()).compareTo(Chess.positionFile(fileRankFinal)))==1){//checks if the file diff is max 1
            return true;
        }

        //compiler
        return false;
    }

}
