package chess;

public class Bishop extends Piece{
    //constructor
    public Bishop (PieceType pieceType, PieceFile pieceFile, int pieceRank, String color){
        super(pieceType, pieceFile, pieceRank, color);
    }


    public boolean canMove(String fileRankfinal){
        //sets values of diffrence in ranks and file of current and end positions
        int rankDiff = this.pieceRank - Chess.positionRank(fileRankfinal);
        int fileDiff = (this.pieceFile.toString().charAt(0) - 96) - (Chess.positionFile(fileRankfinal).charAt(0) - 96);
        int numIterations = Math.abs(rankDiff);
        //Check if given rankFileFinal is a diagonal so this peice.rank - rank final must equal this peice file - file
        if((Math.abs(rankDiff) != Math.abs(fileDiff)) || rankDiff == 0 || fileDiff == 0){
            return false;
        }
        //For loop section to check for the 4 diagonals make sure that the move is not out of bounds
        //if in Q1 meaning end is +,+; file is x and rank is y
        if(fileDiff < 0 && rankDiff < 0){
            //interates through diagonal in +,+ direction
            for(int i = 1; i <= numIterations; i++){
                //calculated how many diagagonal squares out and if they are the same
                int currRank = this.pieceRank + i;
                int intCurrFile = this.pieceFile.toString().charAt(0) - 96 + i;
                if(currRank < 9 && intCurrFile < 9){
                    //combines rank and file to shorten retyping
                    String currPosition = Character.toString(((char)(intCurrFile + 96))) + currRank;
                    //if last square check if empty or if space is occupied by piece of diffrent color
                    if((i == numIterations) && ((Chess.openSquare(currPosition) || ((Chess.pieceAt(currPosition) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(currPosition)).getColor()))))){
                        return true;
                    }
                    //checks if there is anything in the way to rankFilefinal if there is return false
                    if(!Chess.openSquare(currPosition)){
                        return false;
                    }
                }
            }
            //if nothing in the way return true
            return true;
        }
        //if in Q2 meaning end is -,+; file is x and rank is y
        else if(fileDiff > 0 && rankDiff < 0){
            //interates through diagonal in -,+ direction
            for(int i = 1; i <= numIterations; i++){
                //calculated how many diagagonal squares out and if they are the same
                int currRank = this.pieceRank + i;
                int intCurrFile = this.pieceFile.toString().charAt(0) - 96 - i;
                if(currRank < 9 && intCurrFile > 0){
                    //combines rank and file to shorten retyping
                    String currPosition = Character.toString(((char)(intCurrFile + 96))) + currRank;
                    //if last square check if empty or if space is occupied by piece of diffrent color
                    if((i == numIterations) && ((Chess.openSquare(currPosition) || ((Chess.pieceAt(currPosition) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(currPosition)).getColor()))))){
                        return true;
                    }
                    //checks if there is anything in the way to rankFilefinal if there is return false
                    if(!Chess.openSquare(currPosition)){
                        return false;
                    }
                }
            }
            //if nothing in the way return true
            return true;
        }
        //if in Q3 meaning end is -,-; file is x and rank is y
        else if(fileDiff > 0 && rankDiff > 0){
            //interates through diagonal in -,- direction
            for(int i = 1; i <= numIterations; i++){
                //calculated how many diagagonal squares out and if they are the same
                int currRank = this.pieceRank - i;
                int intCurrFile = this.pieceFile.toString().charAt(0) - 96 - i;
                if(currRank > 0 && intCurrFile > 0){
                    //combines rank and file to shorten retyping
                    String currPosition = Character.toString(((char)(intCurrFile + 96))) + currRank;
                    //if last square check if empty or if space is occupied by piece of diffrent color
                    if((i == numIterations) && ((Chess.openSquare(currPosition) || ((Chess.pieceAt(currPosition) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(currPosition)).getColor()))))){
                        return true;
                    }
                    //checks if there is anything in the way to rankFilefinal if there is return false
                    if(!Chess.openSquare(currPosition)){
                        return false;
                    }
                }
            }
            //if nothing in the way return true
            return true;
        }
        //if in Q4 meaning end is +,-; file is x and rank is y
        else if(fileDiff < 0 && rankDiff > 0){
            //interates through diagonal in +,- direction
            for(int i = 1; i <= numIterations; i++){
                //calculated how many diagagonal squares out and if they are the same
                int currRank = this.pieceRank - i;
                int intCurrFile = this.pieceFile.toString().charAt(0) - 96 + i;
                if(currRank > 0 && intCurrFile < 9){
                    //combines rank and file to shorten retyping
                    String currPosition = Character.toString(((char)(intCurrFile + 96))) + currRank;
                    //if last square check if empty or if space is occupied by piece of diffrent color
                    if((i == numIterations) && ((Chess.openSquare(currPosition) || ((Chess.pieceAt(currPosition) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(currPosition)).getColor()))))){
                        return true;
                    }
                    //checks if there is anything in the way to rankFilefinal if there is return false
                    if(!Chess.openSquare(currPosition)){
                        return false;
                    }
                }
            }
            //if nothing in the way return true
            return true;
        }
        return false;
    }

}
