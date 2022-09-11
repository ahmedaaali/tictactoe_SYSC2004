import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.awt.Dimension;

/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game in a very
 * simple GUI window.
 * 
 * @author Lynn Marshall
 * @version November 8, 2012
 * 
 * @author Ahmed Ali (101181126)
 * @version April 2, 2022
 */

public class TicTacToeGui  implements ActionListener
{ 
   public static final String PLAYER_X = "X"; // player using "X"
   public static final String PLAYER_O = "O"; // player using "O"
   public static final String EMPTY = " ";  // empty cell
   public static final String TIE = "T"; // game ended in a tie
      
   private Container contentPane; // where everything starts after being put in a jframe
   
   private JPanel buttonPanel; // 3x3 buttons for the grid
      
   private JLabel iconLabel_X, iconLabel_O; // used to temp hold the x or o icon (can be removed)
      
   /* The reset menu item */
   private JMenuItem resetItem;
   
   /* The quit menu item */
   private JMenuItem quitItem;
   
   private JMenuItem newItem, changeItem;
   
   private JLabel info, stats; // info (bottom), stats (top)
   
   private JComponent board[]; // 2nd board, found it easier to use some old code and some new
   
   private Icon start, black, xIcon, oIcon; // all icons to be used
   
   private int SCORE_O, SCORE_X, SCORE_T; //keep the scores
    
   private String player, startingPlayer;   // current player (PLAYER_X or PLAYER_O)

   private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress

   private int numFreeSquares; // number of squares still free

    /** 
    * Constructs a new Tic-Tac-Toe board and sets up the basic
    * JFrame containing a 3x3 grid layout of the game.
    */
   public TicTacToeGui()
   {
        JFrame frame = new JFrame("Tic-Tac-Toe");
        frame.setPreferredSize(new Dimension(700, 700));
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar); // add menu bar to our frame

        JMenu fileMenu = new JMenu("Options"); // create a menu
        menubar.add(fileMenu); // and add to our menu bar
        // we are going to listen to mouse actions on the fileMenu
        //fileMenu.addMouseListener(this);

        resetItem = new JMenuItem("Reset"); // create a menu item called "Reset"
        fileMenu.add(resetItem); // and add to our menu

        quitItem = new JMenuItem("Quit"); // create a menu item called "Quit"
        fileMenu.add(quitItem); // and add to our menu
        
        changeItem = new JMenuItem("Change"); // create a menu item called "Change"
        fileMenu.add(changeItem); // and add to our menu

        newItem = new JMenuItem("New"); // create a menu item called "New"
        fileMenu.add(newItem); // and add to our menu

        // this allows us to use shortcuts (e.g. Ctrl-R and Ctrl-Q)
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // to save typing
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        changeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_MASK));
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
      
        // listen for menu selections
        resetItem.addActionListener(this); 
        changeItem.addActionListener(this);
        newItem.addActionListener(this);
        quitItem.addActionListener(new ActionListener() // create an anonymous inner class
            { // start of anonymous subclass of ActionListener
            // this allows us to put the code for this action here  
            public void actionPerformed(ActionEvent event)
            {
                System.exit(0); // quit
            }
          } // end of anonymous subclass
        ); // end of addActionListener parameter list and statement
                
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3)); //3x3 grid for the tictactoe buttons
        
        start = new ImageIcon("start.png"); //creating the icons now to save time
        black = new ImageIcon("black.png");
        xIcon = new ImageIcon("x.png");
        oIcon = new ImageIcon("o.png");
        
        stats = new JLabel("Statistics:  " + PLAYER_X + " WINS: " + SCORE_X + "  " +
        PLAYER_O + " WINS: " + SCORE_O + "  TIES: " + SCORE_T); // stats of the game
        info = new JLabel("Game in Progress: " + player + "'s turn"); // info each round
        SCORE_X = 0; // initialized to 0
        SCORE_O = 0;
        SCORE_T = 0;
        startingPlayer = PLAYER_X; // normal starting player is x, unless changed
        clearBoard(); // many initializied variables are moved here, since they'll be used many times
        contentPane.add(buttonPanel, BorderLayout.CENTER); // button panel added
        
        iconLabel_X = new JLabel(xIcon);
        iconLabel_O = new JLabel(oIcon);
        
        stats.setHorizontalAlignment(JLabel.LEFT); // left justified
        contentPane.add(stats,BorderLayout.NORTH);
        
        info.setHorizontalAlignment(JLabel.LEFT); // left justified
        contentPane.add(info,BorderLayout.SOUTH); // north and south info

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true); // make it visible
   } 
   
   /**
    * Sets everything up for a new game.  Marks all squares in the Tic Tac Toe board as empty,
    * and indicates no winner yet, 9 free squares and the current player is player X. Add the
    * required buttons and their listeners, to  know what the player chooses.
    */
   private void clearBoard()
   {
      buttonPanel.removeAll();
      buttonPanel.revalidate();
      buttonPanel.repaint(); // to reinitialize buttonpannel every time
      
      board = new JComponent[9]; // board for the buttons (found it easier to use some old code and some new)
      for (int i = 0; i < 9; i++){ // everything that has to do with the buttons
          board[i] = new JButton(start);
          board[i].setEnabled(true);
          buttonPanel.add(board[i]);
          ((JButton)board[i]).addActionListener(this);
      }
      
      winner = EMPTY;
      numFreeSquares = 9;
      player = startingPlayer;     // Player X always has the first turn.
   }
   
   /** This action listener is called when the user clicks on 
    * any of the GUI's buttons. 
    */
   public void actionPerformed(ActionEvent e)
   {
      Object o = e.getSource(); // get the action
      if (o instanceof JButton) { //check if it's a JButton or JMenu
          JButton button = (JButton)o;
          int x = 0; // used to check if there's winner
          if (winner.equals(EMPTY)){ // always checks if winner is already found
              for (int i = 0; i < 9; i++){
                  if (button == board[i]){// changes from button to label of x or o
                        x = i; // used to check if there's winner
                        board[i].setEnabled(false);
                        if (player==PLAYER_X){
                            board[i] = iconLabel_X; // change the iconlabel
                        }
                        else {
                            board[i] = iconLabel_O;
                        }
                        buttonPanel.removeAll(); // remove then add 1 by 1 steps
                        for (int j = 0; j < 9; j++){
                            if (j == i){ // the new one
                                buttonPanel.add(board[j]);
                            }
                            else if (board[j] == iconLabel_X){ // all the old ones
                                buttonPanel.add(new JLabel(xIcon));
                            }
                            else if (board[j] == iconLabel_O){
                                buttonPanel.add(new JLabel(oIcon));
                            }
                            else{
                                buttonPanel.add(board[j]); // non-changed buttons
                            }
                        }
                        buttonPanel.revalidate();
                        buttonPanel.repaint();
                    }
              }
              
              numFreeSquares--;            // decrement number of free squares
        
              // see if the game is over
                 if (haveWinner(x)) {
                    winner = player; // must be the player who just went
                    info.setText("Game Over: " + player + " wins"); // update info
                    if (player==PLAYER_X) {
                        SCORE_X++;
                    }
                    else {
                        SCORE_O++; // update the variables
                    }
                    updateStats();
                    
                    buttonPanel.removeAll(); // here I add the black icon labels
                    for (int i = 0; i < 9; i++){
                        if (board[i] instanceof JButton){
                            buttonPanel.add(new JLabel(black));
                        } else if (board[i].equals(iconLabel_X)){
                            buttonPanel.add(new JLabel(xIcon));
                        } else{
                            buttonPanel.add(new JLabel(oIcon));
                        }
                    }
                    buttonPanel.revalidate(); // apparently always important
                    buttonPanel.repaint();
                }
                 else if (numFreeSquares==0) {
                    winner = TIE; // board is full so it's a tie
                    info.setText("Game Over: Tied Game");
                    SCORE_T += 1;
                    updateStats();
                }else{
                     // change to other player (this won't do anything if game has ended)
                     if (player==PLAYER_X) {
                        player=PLAYER_O;
                    }
                    else {
                        player=PLAYER_X;
                    }
                    info.setText(("Game in Progress: " + player + "'s turn"));
                }
            }
      } else { // it's a JMenuItem
          JMenuItem item = (JMenuItem)o;
          if (item == resetItem) { 
              clearBoard(); // always clear board to start a new round
          }
          if (item == changeItem) { 
              changePlayer();
              clearBoard();
          }
          if (item == newItem) { 
              startingPlayer=PLAYER_X;
              clearBoard();
              SCORE_X = 0;
              SCORE_O = 0;
              SCORE_T = 0;
              updateStats();
          }
        }
   }
   
   /**
    * Updates the new scores in the stats bar.
    */
    private void updateStats()
    {
      stats.setText("Statistics:  " + PLAYER_X + " WINS: " + SCORE_X + "  " +
      PLAYER_O + " WINS: " + SCORE_O + "  TIES: " + SCORE_T);
    }
    
    /**
     * Changing who the first player should be x to o and o to x.
     */
   private void changePlayer(){
        if (startingPlayer==PLAYER_X) {
            startingPlayer=PLAYER_O;
        }
        else {
            startingPlayer=PLAYER_X;
        }
   }

   /**
    * Returns true if filling the given square gives us a winner, and false
    * otherwise.
    *
    * @param int row of square just set
    * @param int col of square just set
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(int x) 
   {
       // unless at least 5 squares have been filled, we don't need to go any further
       // (the earliest we can have a winner is after player X's 3rd move).

       if (numFreeSquares>4) return false;

       // Note: We don't need to check all rows, columns, and diagonals, only those
       // that contain the latest filled square.  We know that we have a winner 
       // if all 3 squares are the same, as they can't all be blank (as the latest
       // filled square is one of them).

       int row = x / 3;
       int col = x % 3;
       
       // check row "row"
       if ( board[3 * row].equals(board[3 * row + 1]) &&
            board[3 * row].equals(board[3 * row + 2]) ) return true;
       
       // check column "col"
       if ( board[col].equals(board[3 + col]) &&
            board[col].equals(board[6 + col]) ) return true;

       // if row=col check one diagonal
       if (row==col)
          if ( board[0].equals(board[4]) &&
               board[0].equals(board[8]) ) return true;

       // if row=2-col check other diagonal
       if (row==2-col)
          if ( board[2].equals(board[4]) &&
               board[2].equals(board[6]) ) return true;

       // no winner yet
       return false;
   }

   /**
    * Detects when the mouse enters the component.  We are only "listening" to the
    * JMenu.  We highlight the menu name when the mouse goes into that component.
    * 
    * @param e The mouse event triggered when the mouse was moved into the component
    */
   public void mouseEntered(MouseEvent e) {
        
   }

   /**
    * Detects when the mouse exits the component.  We are only "listening" to the
    * JMenu.  We stop highlighting the menu name when the mouse exits  that component.
    * 
    * @param e The mouse event triggered when the mouse was moved out of the component
    */
   public void mouseExited(MouseEvent e) {
        
   }
}