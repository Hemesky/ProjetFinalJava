package controller;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import model.IElement;
import model.IMobile;

import model.IModelFacade;
import model.Permeability;
import model.element.mobile.SupraPower;
import view.IViewFacade;



public class ControllerFacade implements IControllerFacade, IOrderPerformer {

    /** The game-thread refresh speed. */
    private static final int speed = 50;

    /** The view. */
    private IViewFacade view;

    /** The model. */
    private IModelFacade model;

    /** The stack order. */
    private UserOrder stackOrder;
    
    /** The monster of type 1. */
    private IMobile monster1;
    
    /** The monster of type 1. */
    private IMobile monster2;

	/** The monster of type 1. */
    private IMobile monster3;
    
    /** The monster of type 1. */
    private IMobile monster4;
    
    /** Array of monster */
    private ArrayList monsters;
    
    /** The Monsters speed counter */
	private int monsterDelay = 1;
    
    /** The Lorann. */
    private IMobile lorann;
    
    /** The Gate. */
    private IElement gate;
    
    /** The Crystal. */
    private IElement crystal;
    
    /** The Super Power of Loran */
    public IMobile power;
    
    /** Store the lastLorannOrder */
    private UserOrder lastLorannOrder;
    
    /** take the value of the lastLorannOrder to know in which direction the power must go */
    private UserOrder powerOrder;
    
    /** Ammo of the player allow instantiation of power */
    private boolean ammo = true;
    
    /** The Power speed counter */
    private int powerDelay = 5;

     /** The boolean to stop game if player finish the level */
    private boolean win;
	
	/** The Monster speed */
	private int mobSpeed = 15;
	
	/** The Power speed */
    private int powerSpeed = 5;

	
    /**
     * Instantiates a new Lorann controller
     * It will be used to move the player, monsters, power and the level and also to check if there is collision, kill ...
     *
     *
     * @param view
     *            the view
     * @param model
     *            the model
     */
	public ControllerFacade(final IViewFacade view, final IModelFacade model) {
		this.setView(view);
	    this.setModel(model);
	    this.clearStackOrder(); //set the user order to NOP so we are sure that the player do not move on spawn
	}

	/**
	 * Drive the game element movement, behavior and threading
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void play() throws InterruptedException, IOException {
		
		
		
		
		/**
		 * 
		 * 
		 * PARTIE TRES IMPORTANTE ! 
		 * 
		 * 
		 * 
		 */
		
		
		lorann = getModel().getLevel().getLorann();
		lorann.alive();
		
		
		gate = getModel().getLevel().getGate();
		crystal = getModel().getLevel().getCrystal();
		
		
		if(getModel().getLevel().getMonster1instance() != false) { 
			monster1 = getModel().getLevel().getMonster1();
			monster1.alive();
			monster1.doNothing();
			}
        if(getModel().getLevel().getMonster2instance() != false) {
        	monster2 = getModel().getLevel().getMonster2();
        	monster2.alive();
        	monster2.doNothing();
        	}
        if(getModel().getLevel().getMonster3instance() != false) {
        	monster3 = getModel().getLevel().getMonster3();
        	monster3.alive();
        	monster3.doNothing();
        	}
        if(getModel().getLevel().getMonster4instance() != false) {
        	monster4 = getModel().getLevel().getMonster4();
        	monster4.alive();
        	monster4.doNothing();
        	}
        
       
        
        
      
        if(getModel().getLevel().getCrystal() == null) {
        	getModel().getLevel().getGate().setPermeability(Permeability.OPENGATE);
        	getView().OpenGateUpdate();
        	}
		
     
        
        
        /**
         * 
         * 
         * BOUCLE DU JEU !
         */
		while (lorann.isAlive() && win == false) { 
            
			Thread.sleep(speed); 
			
			
			if(lorann.isOnCrystall()) {
				
				gate.setPermeability(Permeability.OPENGATE);
	        	crystal.setPermeability(Permeability.PENETRABLE);
				getView().OpenGateUpdate();
			}
			
			
			if(lorann.isOnOpenGate()) win = true;
			
			
			if(lorann.isKilled()) lorann.die();
			
			
			switch (this.getStackOrder()) { //this case execute the method associated to the user order (move, shot, nothing)
                case RIGHT:
                    this.lorann.moveRight();
                    lastLorannOrder=UserOrder.RIGHT;
                    break;
                case LEFT:
                    this.lorann.moveLeft();
                    lastLorannOrder=UserOrder.LEFT;
                    break;
                case UP:
                    this.lorann.moveUp();
                    lastLorannOrder=UserOrder.UP;
                    break;
                case DOWN:
                    this.lorann.moveDown();
                    lastLorannOrder=UserOrder.DOWN;
                    break;
                case DOWNRIGHT:
                    this.lorann.moveDownRight();
                    lastLorannOrder=UserOrder.DOWNRIGHT;
                    break;
                case UPRIGHT:
                    this.lorann.moveUpRight();
                    lastLorannOrder=UserOrder.UPRIGHT;
                    break;
                case DOWNLEFT:
                    this.lorann.moveDownLeft();
                    lastLorannOrder=UserOrder.DOWNLEFT;
                    break;
                case UPLEFT:
                    this.lorann.moveUpLeft();
                    lastLorannOrder=UserOrder.UPLEFT;
                    break;
                case SHOOT:
                	if(ammo) {power = new SupraPower(lorann.getX(), lorann.getY(), getModel().getLevel());
                	getView().PowerSpawn(power);
                	powerOrder=lastLorannOrder;
                	ammo = false;
                	}
                case NOP:
                default:
                	this.lorann.doNothing();
                	break;
			}
			
			if(power!=null)PowerMechanism();
			

			if(monster1!=null && monster1.isAlive()) MonsterIA(monster1);
			if(monster2!=null && monster2.isAlive()) MonsterIA(monster2);
			if(monster3!=null && monster3.isAlive()) MonsterIA(monster3);
			if(monster4!=null && monster4.isAlive()) MonsterIA(monster4);

            
            this.clearStackOrder(); // this reset the user order to NOP so it will not continue to move when you released the key

        }
		if (win != true) {
		lorann.die();
		
        this.getView().displayMessage("You loose"); //when the main loop is break this display the message you loose on a popup 	
		}
		else {
		this.getView().displayMessage("You win");
		}
	}
	
	/**
	 * This function is a kind of IA for monster to go on Lorann
	 */
	private void MonsterIA(IMobile monster) {
		//if the counter of delay match the mob wanted speed then we enter this if to move mob to the player
		if(monsterDelay == mobSpeed) { 
    		monsterDelay=0;
    		//if(monster != null) {
    		//move the monster to the player
			if(lorann.getX() > monster.getX()) {
	            monster.moveRight();
			}
			if(lorann.getX() < monster.getX()) {
	            monster.moveLeft();
			}
			if(lorann.getY() < monster.getY()) {
	            monster.moveUp();
			}
			if(lorann.getY() > monster.getY()) {
	            monster.moveDown();
			}
    		//}
	   	}
	   	//if the counter doesn't match speed then we increment the counter
	   	else monsterDelay++;
	   	
		//go to the function that check if player is on a monster so he has to be killed
		MobKillChecker(monster);

		if(power!=null)PowerKillChecker(monster);
	}

	public void MobKillChecker(IMobile monster) {
		if(lorann.getX()==monster.getX() && lorann.getY()==monster.getY()) {lorann.die();}
	}
	
	public void PowerMechanism() {
		//this if manage the power movement speed & picture change speed
		if(powerDelay == powerSpeed) { 
			powerDelay=0;
			switch (this.powerOrder) {
	        case LEFT:
	            this.power.moveRight();
	            break;
	        case RIGHT:
	            this.power.moveLeft();
	            break;
	        case DOWN:
	            this.power.moveUp();
	            break;
	        case UP:
	            this.power.moveDown();
	            break;
			}
		}
	   	//if the counter doesn't match speed then we increment the counter
	   	else powerDelay++;
	   	
		//if Lorann is on the power then destroy the last power, and get 1 ammo
		if(lorann.getX()==power.getX() && lorann.getY()==power.getY()) {
			power.die();
			ammo = true;
			power = null;
			powerDelay = 5;
		}
	}
	
	public void PowerKillChecker(IMobile monster) {
		
		if(power.getX()==monster.getX() && power.getY()==monster.getY()) {
			monster.die();
			monster=null;
			power.die();
			ammo = true;
			power = null;
			powerDelay = 5;
	}}
	
    /**
     * Write the UserOrder in the stack of order (stackOrder)
     */
	@Override
	public void orderPerform(UserOrder userOrder) throws IOException {
		this.setStackOrder(userOrder);
	}
	
    /**
     * Gets the view.
     *
     * @return the view
     */
    private IViewFacade getView() {
        return this.view;
    }
    
    /**
     * Sets the view.
     *
     * @param view
     *            the view to set
     */
    private void setView(final IViewFacade view) {
        this.view = view;
    }
    
    /**
     * Gets the model.
     *
     * @return the model
     */
    private IModelFacade getModel() {
        return this.model;
    }
    
    /**
     * Sets the model.
     *
     * @param model
     *            the model to set
     */
    private void setModel(final IModelFacade model) {
        this.model = model;
    }
    
    /**
     * Gets the stack order.
     *
     * @return the stack order
     */
    private UserOrder getStackOrder() {
        return this.stackOrder;
    }

    /**
     * Sets the stack order.
     *
     * @param stackOrder
     *            the new stack order
     */
    private void setStackOrder(final UserOrder stackOrder) {
        this.stackOrder = stackOrder;
    }

    /**
     * Clear stack order.
     */
    private void clearStackOrder() {
        this.stackOrder = UserOrder.NOP;
    }

   /**
    * Get the order to perform
    */
    @Override
    public IOrderPerformer getOrderPeformer() {
        return this;
    }

}