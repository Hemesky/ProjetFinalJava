package view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controller.IOrderPerformer;
import controller.UserOrder;
import model.ILevel;
import model.IMobile;
import showboard.BoardFrame;

/**
 * <h1>The Class ViewFacade provides a facade of the View component.</h1>
 *
 * @author Jean-Aymeric DIET jadiet@cesi.fr
 * @version 1.0
 */

public class ViewFacade implements IViewFacade, Runnable, KeyListener {

    /** The Constant width square number of the levels. */
    private static final int squareNumberWidth = 24;
    
    /** The Constant height square number of the levels. */
    private static final int squareNumberHeight = 12;
    
    /** The Constant squareSize to make the window bigger or smaller but keeping the proportions . */
    private static final int squareSize = 50;

    /** The Constant closeView its the window launch by the thread. */
    private Rectangle        closeView;

    /** The level. */
    private ILevel           level;

    /** The order performer. */
    private IOrderPerformer  orderPerformer;

    BoardFrame boardFrame;
    
    Image Door;
    ImageIcon iDoor;
    
    /**
     * Instantiates a new View.
     * It will create the window, a frame and a kind of plate with square to place element of the level
     * All of this his load and shown by the thread
     *
     * @param level
     *            the level
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public ViewFacade(final ILevel level) throws IOException {
        this.setLevel(level); //create in memory the level (x,y,picture)
        //this defines a Rectangle in the closing view that begins at the coordinates 0,0 and whose width and height are those of the level (square unity)
        this.setCloseView(new Rectangle(0, 0, squareNumberWidth, squareNumberHeight));
        //this launch the thread it launch the method run(), apart the all game

        SwingUtilities.invokeLater(this);
    }

   /**
    * Display a message in a popup
    */
    @Override
    public final void displayMessage(final String message) {
        JOptionPane.showMessageDialog(null, message);
    }

   /**
    * Thread that run the window
    */
	@Override
    public final void run() { //this create a new BoardFrame its simply a panel in the window named Lorann
        boardFrame = new BoardFrame("Lorann");
        boardFrame.setDimension(new Dimension(squareNumberWidth, squareNumberHeight)); // set the dimension of the panel to the level (square unity)
        boardFrame.setDisplayFrame(this.getCloseView()); //say what to display in the frame
        boardFrame.setSize(squareNumberWidth * squareSize, squareNumberHeight * squareSize); //set the size of thre frame (pixel unity) 
        boardFrame.addKeyListener(this); //the window can listen to keyboard entry

        for (int x = 0; x < squareNumberWidth; x++) { //this double for set each square to his sprite picture
            for (int y = 0; y < squareNumberHeight; y++) {
                boardFrame.addSquare(this.level.getOnTheLevelXY(x, y), x, y);
            }
        }

        if(getLevel().getMonster1instance() != false) {boardFrame.addPawn(getLevel().getMonster1());} //if instance of monster1 is not false then spawn it on level
        if(getLevel().getMonster2instance() != false) {boardFrame.addPawn(getLevel().getMonster2());}
        if(getLevel().getMonster3instance() != false) {boardFrame.addPawn(getLevel().getMonster3());}
        if(getLevel().getMonster4instance() != false) {boardFrame.addPawn(getLevel().getMonster4());}
        boardFrame.addPawn(getLevel().getLorann()); //this place ('spawn') the mobile element Lorann over a square
        
        this.getLevel().getObservable().addObserver(boardFrame.getObserver()); //the view is registred to be observed by the level
        boardFrame.setVisible(true); //make the game appear in first plan
    }
	
	/**
	 * Function to change the picture of the gate and crystal when the player take the crystal
	 */
	public void OpenGateUpdate() {
		try {
			getLevel().getGate().getImage().getGraphics().drawImage(ImageIO.read(new File("C:\\Users\\User\\git\\Lorann-Remastered\\main\\sprites\\gate_open.png")),0,0, null); //this update the picture of the gate from close to open
			if (getLevel().getCrystal() != null) //if the level get a crystal then we set it to black, else we didn't do anything
				getLevel().getCrystal().getImage().getGraphics().drawImage(ImageIO.read(new File("C:\\Users\\User\\git\\Lorann-Remastered\\main\\sprites\\BlackTile.jpg")),0,0, null);//this update the picture of the crystal to black
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to change the picture of the purse that the player take
	 */
	public void PurseUpdate() {
		/*try {
			getLevel().getGate().getImage().getGraphics().drawImage(ImageIO.read(new File("sprites/gate_open.png")),0,0, null); //this update the picture of the gate from close to open
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public void PowerSpawn (IMobile power) {
		boardFrame.addPawn(power);
	}
	
	
    /**
     * Key code to user order.
     * It choose the right user order in function of the keycode
     *
     * @param keyCode
     *            the key code
     * @return the user order
     */
    static UserOrder keyCodeToUserOrder(final int keyCode) {
        UserOrder userOrder;
                
        switch (keyCode) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
            	userOrder = UserOrder.RIGHT;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_Q:
                userOrder = UserOrder.LEFT;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_Z:
                userOrder = UserOrder.UP;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                userOrder = UserOrder.DOWN;
                break;
            case KeyEvent.VK_X:
                userOrder = UserOrder.DOWNRIGHT;
                break;
            case KeyEvent.VK_W:
                userOrder = UserOrder.DOWNLEFT;
                break;
            case KeyEvent.VK_E:
                userOrder = UserOrder.UPRIGHT;
                break;
            case KeyEvent.VK_A:
                userOrder = UserOrder.UPLEFT;
                break;
            case KeyEvent.VK_SPACE:
                userOrder = UserOrder.SHOOT;
                break;
            default:
                userOrder = UserOrder.NOTHING;
                break;
        }
        return userOrder;
    }

    /**
     * Not used
     */
    @Override
    public void keyTyped(final KeyEvent keyEvent) {
        // Not used
    }

    /**
     * Catch the user keyPressed, conver it and send it to the controller
     */
    @Override
    public final void keyPressed(final KeyEvent keyEvent) {
        try { 
        	//we get the keycode and send it to keycodeUserOrder to transform it into a userOrder
        	//then we send the userOrder to orderPerfrom who will stack the order in stackOrder
            this.getOrderPerformer().orderPerform(keyCodeToUserOrder(keyEvent.getKeyCode()));
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Not used
     */
    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        // Not used
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    private ILevel getLevel() {
        return this.level;
    }

    /**
     * Sets the level.
     *
     * @param level
     *            the new level
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void setLevel(final ILevel level) throws IOException {
        this.level = level;
        for (int x = 0; x < ViewFacade.squareNumberWidth; x++) { //this double for made the level
            for (int y = 0; y < ViewFacade.squareNumberHeight; y++) {
                this.getLevel().getOnTheLevelXY(x, y).getSprite().loadImage(); //it place in memory the picture corresponding to a postion X Y
            }
        }
    }


    /**
     * Gets the close view.
     *
     * @return the close view
     */
    private Rectangle getCloseView() {
        return this.closeView;
    }

    /**
     * Sets the close view.
     *
     * @param closeView
     *            the new close view
     */
    private void setCloseView(final Rectangle closeView) {
        this.closeView = closeView;
    }

    /**
     * Gets the order performer.
     *
     * @return the order performer
     */
    private IOrderPerformer getOrderPerformer() {
        return this.orderPerformer;
    }

    /**
     * Sets the order performer.
     *
     * @param orderPerformer
     *            the new order performer
     */
    public final void setOrderPerformer(final IOrderPerformer orderPerformer) {
        this.orderPerformer = orderPerformer;
    }
}



