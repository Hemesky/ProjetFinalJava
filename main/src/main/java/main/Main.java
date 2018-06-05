package main;

import java.io.IOException;
import java.sql.SQLException;

import controller.ControllerFacade;
import controller.IControllerFacade;
import model.IModelFacade;
import model.ModelFacade;
import view.IViewFacade;
import view.ViewFacade;

public class Main {
	   
	
	public static void main(final String[] args) throws IOException, InterruptedException, SQLException {
	
		final IModelFacade model = new ModelFacade(1); 									//change the number to the wanted level
        final IViewFacade view = new ViewFacade(model.getLevel()); 						//we gave to the view the level and all element that the level contain
        final IControllerFacade controller = new ControllerFacade(view, model); 		//the controller got the view and model to control them
        view.setOrderPerformer(controller.getOrderPeformer()); 							//this enable the view be update when there is a player movement from the controller

        controller.play(); 																//launch the 'infinite' loop of the game
    }
}

    

