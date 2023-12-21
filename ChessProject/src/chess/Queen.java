package chess;

public class Queen extends Piece{
    //constructor
    public Queen (PieceType pieceType, PieceFile pieceFile, int pieceRank, String color){
        super(pieceType, pieceFile, pieceRank, color);
    }


    public boolean canMove(String fileRankfinal){

        return (canMoveBishop(fileRankfinal) || canMoveRook(fileRankfinal));
    }

    public boolean canMoveBishop(String fileRankfinal){
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

    public boolean canMoveRook(String fileRankfinal){
        //checks if user is trying to move in the same rank {1..8} as peice 
        if(Chess.positionRank(fileRankfinal) == this.pieceRank && !((this.pieceFile).name()).equals(Chess.positionFile(fileRankfinal))){
            //check for which file has min value and for loop should go form min to max, also diffrence to ceck out of bounds
            int min, max;
            if(((this.pieceFile).name().compareTo(Chess.positionFile(fileRankfinal))) > 0){ 
                //should be -97 but -96 to have file a be equivilant to rank 1
                min = Chess.positionFile(fileRankfinal).charAt(0) - 96;
                max = (this.pieceFile).name().charAt(0) - 96;
            }
            else{
                max = Chess.positionFile(fileRankfinal).charAt(0) - 96;
                min = (this.pieceFile).name().charAt(0) - 96;
            }

            //create for loop to interate through the file from current file to endFile and check if there is anything in the way            
            for(int i = min + 1; i <= max; i++){
                //if min = max and thier colors are diffrent or if is an opensquare
                if(i == max){
                    if((((this.pieceFile).name().compareTo(Chess.positionFile(fileRankfinal))) > 0)  && (Chess.openSquare(fileRankfinal) || ((Chess.pieceAt(fileRankfinal) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(fileRankfinal)).getColor())))){
                        return true;
                    }
                    if((((this.pieceFile).name().compareTo(Chess.positionFile(fileRankfinal))) < 0) && ((Chess.openSquare(Character.toString(((char)(i+96))) + this.pieceRank)) || ((Chess.pieceAt(fileRankfinal) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(fileRankfinal)).getColor())))){
                        return true;
                    }
                }
                //checks if there is anything in the way to min=max if there is return false
                if(!Chess.openSquare(Character.toString(((char)(i+96))) + this.pieceRank)){
                    return false;
                }
            }
            //return true if there is nothing blocking the way
            return true;

        }
        
        //checks if user is trying to move in the same rank {a..h} as peice
        if(((this.pieceFile).name()).equals(Chess.positionFile(fileRankfinal)) && Chess.positionRank(fileRankfinal) != this.pieceRank){
            //check for which rank has min value and for loop should go form min to max, also diffrence to ceck out of bounds
            int min, max;
            if((this.pieceRank - Chess.positionRank(fileRankfinal)) > 0){ 
                min = Chess.positionRank(fileRankfinal);
                max = this.pieceRank;
            }
            else{
                max = Chess.positionRank(fileRankfinal);
                min = this.pieceRank;
            }
            //create for loop to interate through the rank from current rank to endRank and check if there is any thing in the way
            for(int i = min + 1; i <= max; i++){
                //if min = max and thier colors are diffrent or if is an opensquare
                if(i == max){
                    //if piece is trying to move down then check if the down spot is empty or has oppositie color
                    if(((this.pieceRank - Chess.positionRank(fileRankfinal)) > 0) && (Chess.openSquare(fileRankfinal)  || ((Chess.pieceAt(fileRankfinal) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(fileRankfinal)).getColor())))){
                        return true;
                    }
                    //if piece is trying to move up then check if up spot is empty or has oppsoite color
                    if(((this.pieceRank - Chess.positionRank(fileRankfinal)) < 0) && ((Chess.openSquare((this.pieceFile).name() + i))  || ((Chess.pieceAt(fileRankfinal) != null) && !this.color.equalsIgnoreCase(((Piece)Chess.pieceAt(fileRankfinal)).getColor())))){
                        return true;
                    }
                }
                //checks if there is anything in the way to min=max if there is return false
                if(!Chess.openSquare((this.pieceFile).name() + i)){
                    return false;
                }
            }
            //return true if there is nothing blocking the way
            return true;
        }

        //compiler
        return false;
    }


}
