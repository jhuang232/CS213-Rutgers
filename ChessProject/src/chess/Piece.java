package chess;

public abstract class Piece extends ReturnPiece{
    //fields
    protected String color;

    //constructor 
    public Piece (PieceType piecetype, PieceFile pieceFile, int pieceRank, String color){
        super(); //ReturnPiece has default const
        //need to set those super values manually
        this.pieceType = piecetype;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
        this.color = color;
    }


    //abstract methods bc it is dependent on piece 

    /**
     *  every piece moves
     * @param String the desired location of the piece from its curretn location
     * 
     */
    public void move(String fileRankFinal){
        this.pieceFile = PieceFile.valueOf(Chess.positionFile(fileRankFinal));
        this.pieceRank = Chess.positionRank(fileRankFinal);
            
    }

    /** 
     * checks if a piece can move to a specifc fileRank 
     * @param String fileRank - the desired fileRank
     * 
     * @return boolean to see if the piece is capabel of making that move 
     */
    //was orignally protected but canMove is needed to check for Checks
    public abstract boolean canMove(String fileRankfinal);
    //need helper method to check if first move is legal 
    
    public String getColor(){
        return color;
    }


}
