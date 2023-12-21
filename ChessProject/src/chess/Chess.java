/**
 * Chess Project for CS213 under Sesh Venagupal
 * 
 * @author Jeffrey Tang 
 * @author Jason Huang
 */

package chess;

import java.util.ArrayList;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPiece.PieceType;
import chess.ReturnPlay.Message;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess {
	
	enum Player { white, black }
	
	//I need a board to refer to
	private static ReturnPlay board;
    //player turn - default white
    private static Player turnPlayer = Player.white;
	//move stack
	private static ArrayList<String> moveHistory;
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		//check for player turn
		//format for move: "initial end (draw?)"
		//we need to remove multiple spaces replaceAll("\\s+""," ")
		//we need to remove starting and trailing spaces --> trim()
		//we need to make sure all the inputs are lowercase so our program can read all ACSII properly
		String[] inputs = (move.replaceAll("\\s+", " ").trim()).split(" ");

		//need to ensure proper number of arguments
		if(inputs.length<2) {
			board.message = Message.ILLEGAL_MOVE;
			return board; 
		}

		inputs[0] = inputs[0].toLowerCase();
		inputs[1] = inputs[1].toLowerCase();

        //check for resign
		if(inputs[0].equals("resign")){
			board.message = (turnPlayer==Player.white) ? Message.RESIGN_BLACK_WINS : Message.RESIGN_WHITE_WINS;
			return board;
		}
		//check if a piece exists in that spot 
		Piece currentPiece = (Piece)pieceAt(inputs[0]);
		

		//empty spot? If the piece color is not the same as turn player
		//if the starting is same as ending, attempt to capture own piece
		//make sure that if castling this statement gets skipped
		if(currentPiece==null || inputs[0].equals(inputs[1]) || !((currentPiece.getColor()).equals((turnPlayer).name()))
		    || (((Piece)pieceAt(inputs[1]))!=null && ((Piece)pieceAt(inputs[1])).getColor().equals((turnPlayer).name()))){
			board.message = Message.ILLEGAL_MOVE;
			return board;
		}
		//else normal move or enpassant or castling
		else if(currentPiece.canMove(inputs[1]) || isEnpassant(currentPiece, inputs[1]) || isCastle(currentPiece, inputs[1])){
			//check if a piece is at destination
			Piece destinationPiece = (isEnpassant(currentPiece, inputs[1])) ? 
										(Piece)pieceAt(moveHistory.get(moveHistory.size()-1).split(" ")[1]) //get the piece at the end position of the last move
										: (Piece)pieceAt(inputs[1]); //if not enpassant just do the normal thing (remove piece at end position)

			//message is null //if the move is possible reset message. 
			board.message = null;

			//need to remove any captured piece
			if(destinationPiece!=null){
				board.piecesOnBoard.remove(destinationPiece);
			} 

			//castling
			boolean castled = isCastle(currentPiece, inputs[1]);
			//disgusting and not sure if works yet
			if(castled){
				String rookPos = ('e' > inputs[1].charAt(0))? ("a" + currentPiece.pieceRank) : ("h" + currentPiece.pieceRank);
				Piece other = ((Piece)pieceAt(rookPos));
				//if king side castle
				if(currentPiece.pieceFile.toString().charAt(0) < inputs[1].charAt(0)){
					currentPiece.move(inputs[1]);
					other.move("f" + currentPiece.pieceRank);
					((King)currentPiece).setFirstMove(false);
					((Rook)other).setFirstMove(false);
				}
				//else queen side castle
				else{
					currentPiece.move(inputs[1]);
					other.move("d" + currentPiece.pieceRank);
					((King)currentPiece).setFirstMove(false);
					((Rook)other).setFirstMove(false);
				}
			}else{
				//actual move, 
				currentPiece.move(inputs[1]);
				//need to manually set king or rook's first move to false as canMove check will set firstMove to false when enters method
				switch(currentPiece.pieceType.toString().charAt(1)){
					case 'K':
						((King)currentPiece).setFirstMove(false);
						break;
					case 'R':
						((Rook)currentPiece).setFirstMove(false);
						break;
				}
			}
			//Remark: my pin algo also checks for checks I think
			//if the king is in check all moves are illegal until king is no longer in check

			//check for if piece is supposed to be pinned 
			//we want the move to go through and see if that causes any problems
			King allyKing = (King)findKing(turnPlayer.name());
			//allyKing loc
			String allyKingLoc = allyKing.pieceFile.name() + allyKing.pieceRank;
			//check all pieces
			for(ReturnPiece atkPiece: board.piecesOnBoard){
				//if a piece of enemy color can move onto ally king after the move the move is illegal 
				if(Player.valueOf(((Piece)atkPiece).getColor())!=turnPlayer && ((Piece)atkPiece).canMove(allyKingLoc)){
					if(castled){
						//returns to before castle
						String rookPos = ('e' > inputs[1].charAt(0))? ("a" + currentPiece.pieceRank) : ("h" + currentPiece.pieceRank);
						String rookCastlePos = ('e' > inputs[1].charAt(0))? ("d" + currentPiece.pieceRank) : ("f" + currentPiece.pieceRank);
						Piece other = ((Piece)pieceAt(rookCastlePos));
						currentPiece.move(inputs[0]);
						other.move(rookPos);
						((King)currentPiece).setFirstMove(true);
						((Rook)other).setFirstMove(true);
					}else{
						//we move the piece back normally
						currentPiece.move(inputs[0]);
						//return any captured pieces
						if(destinationPiece!=null){
							board.piecesOnBoard.add(destinationPiece);
						}
					}
					//and say it was a bad move
					board.message = Message.ILLEGAL_MOVE;
					return board;
				}
			}
	
			//needs to be afer the move to chekc for immediate promotion and then before check bc new piece can cause a check
			//check for pawn promotion
			//find all pawn pieces
			//check if a pawn is 1 or 8
			if((currentPiece.pieceType==PieceType.WP && currentPiece.pieceRank==8)//check white pawns that can promote
				|| (currentPiece.pieceType==PieceType.BP && currentPiece.pieceRank==1)){//check black pawns that can promote
				
				//need to see if third argument exists
				String promotionType;
				if(inputs.length!=3){ //theres nothing specified
					promotionType = "Q";
				} else if (inputs[2].contains("Q") || inputs[2].contains("N") || inputs[2].contains("B")
					|| inputs[2].contains("R")){//proper call
					promotionType = inputs[2];
				} else {
					//improper promotion call
					//Venagupal said that this should be the defined behavior if user said "e7 e8 n" where "n" would be an invalid promotion
					currentPiece.move(inputs[0]); //undo move
					//return any captured pieces
					if(destinationPiece!=null){
						board.piecesOnBoard.add(destinationPiece);
					}
					board.message = Message.ILLEGAL_MOVE;
					return board;
				}
				String currPieceColor = currentPiece.getColor().substring(0,1).toUpperCase();
				String promotePieceType = currPieceColor + promotionType;

				//create new class
				ReturnPiece pawnPromoteTo;
				switch(promotionType){
					case "N":
						pawnPromoteTo = new Knight(PieceType.valueOf(promotePieceType), currentPiece.pieceFile,
						 currentPiece.pieceRank, currentPiece.getColor());
						break;
					case "R":
						pawnPromoteTo = new Rook(PieceType.valueOf(promotePieceType), currentPiece.pieceFile,
						 currentPiece.pieceRank, currentPiece.getColor()); 
						break;
					case "B":
						pawnPromoteTo = new Bishop(PieceType.valueOf(promotePieceType), currentPiece.pieceFile,
						 currentPiece.pieceRank, currentPiece.getColor()); 
						break;
					case "Q": default:
						pawnPromoteTo = new Queen(PieceType.valueOf(promotePieceType), currentPiece.pieceFile,
						 currentPiece.pieceRank, currentPiece.getColor()); 
						break;
				}
				//make the swap
				board.piecesOnBoard.remove(currentPiece);
				board.piecesOnBoard.add(pawnPromoteTo);
			}

			//check for checks
			//a King is in check iff a piece can move onto the square the king is on
			//get enemy King obj
			King enemyKing = (King)findKing((turnPlayer==Player.white) ? Player.black.name() : Player.white.name());
			//king location
			String enemyKingLoc = enemyKing.pieceFile.name() + enemyKing.pieceRank;
			for(ReturnPiece atkPiece: board.piecesOnBoard){
				if(Player.valueOf(((Piece)atkPiece).getColor())==turnPlayer && ((Piece)atkPiece).canMove(enemyKingLoc)){
					board.message = Message.CHECK;
				}
			}

			//checkmate somewhere
			//*checkmate happens when current message is check 
			if(board.message == Message.CHECK && checkmate(enemyKingLoc)){

				//if it gets to all the way down here then its checkmate
				board.message = (turnPlayer==Player.white) ? Message.CHECKMATE_WHITE_WINS : Message.CHECKMATE_BLACK_WINS;
			}
			
			
		} else {
			//smth blocks move or piece cannot move there
			board.message = Message.ILLEGAL_MOVE;
			return board;
		}

		//what needs to be done after a successful move

		//record move and piece that made that move
		moveHistory.add(inputs[0]+ " " + inputs[1] + " " + currentPiece.pieceType.name());
		//change player turn
		turnPlayer = (turnPlayer==Player.white) ? Player.black : Player.white;
		//check for draw offers
		if(inputs.length >= 3 && inputs[inputs.length-1].equals("draw?")){
			board.message = Message.DRAW;
		}

		return board;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		/* FILL IN THIS METHOD */

		//generating pieces to mass add
		ArrayList<ReturnPiece> initialstate = new ArrayList<>();
		//white
		//2 rooks
		initialstate.add(new Rook(PieceType.WR, PieceFile.a, 1, "white"));
		initialstate.add(new Rook(PieceType.WR, PieceFile.h, 1, "white"));
		//2 knights
		initialstate.add(new Knight(PieceType.WN, PieceFile.b, 1, "white"));
		initialstate.add(new Knight(PieceType.WN, PieceFile.g, 1, "white"));
		//2 bishops
		initialstate.add(new Bishop(PieceType.WB, PieceFile.c, 1, "white"));
		initialstate.add(new Bishop(PieceType.WB, PieceFile.f, 1, "white"));
		//queen
		initialstate.add( new Queen(PieceType.WQ, PieceFile.d, 1, "white"));
		//king
		initialstate.add(new King(PieceType.WK, PieceFile.e, 1, "white"));
		//8 pawns 
		initialstate.add(new Pawn(PieceType.WP, PieceFile.a, 2, "white"));
		initialstate.add(new Pawn(PieceType.WP, PieceFile.b, 2, "white"));   
		initialstate.add(new Pawn(PieceType.WP, PieceFile.c, 2, "white")); 
		initialstate.add(new Pawn(PieceType.WP, PieceFile.d, 2, "white")); 
		initialstate.add(new Pawn(PieceType.WP, PieceFile.e, 2, "white")); 
		initialstate.add(new Pawn(PieceType.WP, PieceFile.f, 2, "white")); 
		initialstate.add(new Pawn(PieceType.WP, PieceFile.g, 2, "white")); 
		initialstate.add(new Pawn(PieceType.WP, PieceFile.h, 2, "white")); 

		//black
		//2 rooks
		initialstate.add(new Rook(PieceType.BR, PieceFile.a, 8, "black"));
		initialstate.add(new Rook(PieceType.BR, PieceFile.h, 8, "black"));
		//2 knights
		initialstate.add(new Knight(PieceType.BN, PieceFile.b, 8, "black"));
		initialstate.add(new Knight(PieceType.BN, PieceFile.g, 8, "black"));
		//2 bishops
		initialstate.add(new Bishop(PieceType.BB, PieceFile.c, 8, "black"));
		initialstate.add(new Bishop(PieceType.BB, PieceFile.f, 8, "black"));
		//queen
		initialstate.add( new Queen(PieceType.BQ, PieceFile.d, 8, "black"));
		//king
		initialstate.add(new King(PieceType.BK, PieceFile.e, 8, "black"));
		//8 pawns 
		initialstate.add(new Pawn(PieceType.BP, PieceFile.a, 7, "black"));
		initialstate.add(new Pawn(PieceType.BP, PieceFile.b, 7, "black"));   
		initialstate.add(new Pawn(PieceType.BP, PieceFile.c, 7, "black")); 
		initialstate.add(new Pawn(PieceType.BP, PieceFile.d, 7, "black")); 
		initialstate.add(new Pawn(PieceType.BP, PieceFile.e, 7, "black")); 
		initialstate.add(new Pawn(PieceType.BP, PieceFile.f, 7, "black")); 
		initialstate.add(new Pawn(PieceType.BP, PieceFile.g, 7, "black")); 
		initialstate.add(new Pawn(PieceType.BP, PieceFile.h, 7, "black")); 

		//add all to board
		board = new ReturnPlay();
		board.piecesOnBoard = initialstate;
		board.message = null;

		//white goes first
		turnPlayer = Player.white;

		//resetMovehistory
		moveHistory = new ArrayList<>();
	}



	//utility methods

	/**
	 * from a position I want file as a String 
	 * @param String the position itself. ie a2, e5, etc.
	 * 
	 * @return String the file, a-h, of the positon
	 */
	public static String positionFile(String position){
		return position.charAt(0) + "";
	}

	/**
	 * from a position I want rank as int
	 * @param String the position itself. ie a2, e5, etc.
	 * 
	 * @return int the rank of the position
	 */
	public static int positionRank(String position){
		return Integer.parseInt(position.substring(1));
	}

	//check if a square is open
	public static boolean openSquare(String position){
        return (pieceAt(position)==null);
    }

	//check for piece at a position
	public static ReturnPiece pieceAt(String position){
        for(ReturnPiece activePiece: board.piecesOnBoard){
			if(activePiece instanceof Piece && ((activePiece.pieceFile).name()).equals(positionFile(position))
			    && activePiece.pieceRank==Chess.positionRank(position)){
                return activePiece;
			}
		}
		return null;
	}


	/**
	 * This method will find the King
	 * @return return King piece object
	 */
	private static ReturnPiece findKing(String color){
		for(ReturnPiece boardPiece: board.piecesOnBoard){
			if(boardPiece instanceof King && (((Piece)boardPiece).getColor()).equals(color)){
				return boardPiece;//design to choose to return king piece itself so we can set inCheck field
			}
		}
		return null;
	}

	//handling enpassant
	//the canMove() of pawn will say no so we need to treat enpassant as a special move 
	private static boolean isEnpassant(Piece currentPiece, String moveFinal){
		//if no moveHistory just return false 
		if(moveHistory.size()==0) return false;

		//check last move 
		String[] lastMove = moveHistory.get(moveHistory.size()-1).split(" ");
		String lmInitial = lastMove[0];
		String lmFinal = lastMove[1];
		//if last move was a pawn
		PieceType lmPiece = PieceType.valueOf(lastMove[2]);
		//and currentPiece is also a pawn
		boolean pieceAgreement =  ((lmPiece == PieceType.WP && currentPiece.pieceType == PieceType.BP) || (lmPiece == PieceType.BP && currentPiece.pieceType == PieceType.WP));
		//if it was a double jump and end position should be in the same row
		boolean rankAgreement = (Math.abs(Chess.positionRank(lmInitial)-Chess.positionRank(lmFinal))==2) 
			&& Chess.positionRank(lmFinal)==currentPiece.pieceRank;
		//and it is 1 adjacent to currentPiece and end position should be in the file
		boolean fileAgreement = (Math.abs(Chess.positionFile(currentPiece.pieceFile.name()).compareTo(Chess.positionFile(lmFinal)))==1)
			&& Chess.positionFile(lmFinal).equals(Chess.positionFile(moveFinal));

		return pieceAgreement && rankAgreement && fileAgreement;
	}

	private static boolean checkmate(String enemyKingLoc){
		//the idea is to generate every moves on the board for a given piece on the enemy team
		//what is enemy team atm
		String enemyColor = (turnPlayer==Player.white) ? "black" : "white";
		//so we need to generate the set of moves
		for(int i=0;i<board.piecesOnBoard.size();i++){
			ReturnPiece defendPiece = (board.piecesOnBoard).get(i);
			if(((Piece)defendPiece).getColor().equals(enemyColor)){
				//generate the moves
				ArrayList<String> moveSet = new ArrayList<>();
				//start with file
				for(char r='a';r<'i';r++){
					//see if the piece can move to a given square and we are going by rows
					for(int c=1; c<9;c++){
						//check elements in row [j column]
						//if the move is possible add it to moveSet
						if(((Piece)defendPiece).canMove(r+""+c)){
							moveSet.add(r+""+c);
						}
					}
				}

				//get defending piece original location to revert to
				String defendPieceLoc = ((Piece)defendPiece).pieceFile.name() + ((Piece)defendPiece).pieceRank;

				//now try every move 
				for(int m=0;m<moveSet.size();m++){
					String potentialMove = moveSet.get(m);
					//CAPTURE LOGIC
					ReturnPiece tempCapture = pieceAt(potentialMove);
					//temperarily remove the piece at potentialMove since we already know its a legal move
					if(tempCapture!=null){
						board.piecesOnBoard.remove(tempCapture);
						i--;
					} 

					//perform the move
					((Piece)defendPiece).move(potentialMove);	

					String oldKingLoc = ""; 
					if(defendPiece instanceof King){
						oldKingLoc = enemyKingLoc;
						enemyKingLoc = potentialMove;
					}

					boolean enemyKingInCheck = false;
					//now check if this move gets the enemy King out of check
					//go through every piece and see if a piece can still 
					for(ReturnPiece atkPiece: board.piecesOnBoard){
						//so if an ally piece can still hit the king

						//find enemy pieces
						if(Player.valueOf(((Piece)atkPiece).getColor())!=turnPlayer ){
							//converse
						//to invert it so we can look at ally pieces that can move to the enemy
						} else if(((Piece)atkPiece).canMove(enemyKingLoc)){
							//this means another piece still checks the King
							enemyKingInCheck = true;
							break;
						}

					}
					//move the piece back to original spot
					((Piece)defendPiece).move(defendPieceLoc);

					//if the end position is not empty put it back into the list, no particular order so the end is fine
					//everything in this program is linear
					if(tempCapture!=null){
						board.piecesOnBoard.add(tempCapture);
						i++;
					} 
					
					if(defendPiece instanceof King){
						enemyKingLoc = oldKingLoc;
					}
					//if the king is not in check meaning there exists a legal move
					if(!enemyKingInCheck){
						return false;
					}

				}
			}
		}
		return true;//if it gets down here then there is no legal moves found
	}

	
	//handling castling
	//can move of king will say no so need to treat as special move
	private static boolean isCastle(Piece currentPiece, String moveFinal){
		if(board.message==Message.CHECK) return false;

		//begin castling logic
		ArrayList<String> bRLocation = new ArrayList<String>();
		ArrayList<String> wRLocation = new ArrayList<String>();
		//adds c1, g1, c8, g8 to corresponding arraylists
		for(int i = 3; i<=7; i+=4){
			wRLocation.add(Character.toString(i + 96) + "1");
			bRLocation.add(Character.toString(i + 96) + "8");
		}

		String rookPos = ('e' > moveFinal.charAt(0))? ("a" + currentPiece.pieceRank) : ("h" + currentPiece.pieceRank);
        //checks to see if trying to castle if fileRankfinal is equal to a rook position (a1 or h1) or (a8 or h8) also check for king color
        //also checkes if the king has moved already and if rook moved already
        if((currentPiece.pieceType.toString().equals("WK") && (wRLocation.contains(moveFinal))) 
            || (currentPiece.pieceType.toString().equals("BK") && (bRLocation.contains(moveFinal)))
            && Chess.pieceAt(rookPos)!= null && ((Rook)Chess.pieceAt(rookPos)).hasFirstMove() && ((King)currentPiece).hasFirstMove()){
            //check to see what has lower file set that to min and then interate through if there are no peices inbetween return true
            int start = (moveFinal.charAt(0) < currentPiece.pieceFile.toString().charAt(0))? 1: 5;        
            int stop = (start == 1)? 5: 8;
            for(int i = start + 1; i < stop; i++){
                //checks if there is anything in the way to stop if there is return false
                if(!Chess.openSquare(Character.toString(((char)(i+96))) + currentPiece.pieceRank)){
                    return false;
                } 
            }

			//need to check if the King is safe to move to those two squares 
			for (ReturnPiece atkPiece : board.piecesOnBoard){
				if(start==5  //king side
					&& ((Piece)atkPiece).canMove("f"+currentPiece.pieceRank)//it can go to f1 or f8
					&& !(currentPiece.getColor().equals(((Piece)atkPiece).getColor()))){//and the colors are not the same
						return false;
				} else if(start==1//queen side
					&& ((Piece)atkPiece).canMove("d"+currentPiece.pieceRank)//it can go to d1 or d8
					&& !(currentPiece.getColor().equals(((Piece)atkPiece).getColor()))){//and the colors are different
						return false;
				}
			}

            return true;
        }
		//else normal king move
		return false;
	}
}
