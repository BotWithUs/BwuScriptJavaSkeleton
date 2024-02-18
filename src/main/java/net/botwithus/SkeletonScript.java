package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.util.Random;

public class SkeletonScript extends LoopingScript {

    private BotState botState = BotState.IDLE;
    private boolean someBool = true;
    private Random random = new Random();

    enum BotState {
        //define your own states here
        IDLE,
        SKILLING,
        BANKING,
        //...
    }

    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);
    }

    @Override
    public void onLoop() {
        //Loops every 100ms by default, to change:
        //this.loopDelay = 500;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000,7000));
            return;
        }
        switch (botState) {
            case IDLE -> {
                //do nothing
                println("We're idle!");
                Execution.delay(random.nextLong(1000,3000));
            }
            case SKILLING -> {
                //do some code that handles your skilling
                Execution.delay(handleSkilling(player));
            }
            case BANKING -> {
                //handle your banking logic, etc
            }
        }
    }

    private long handleSkilling(LocalPlayer player) {
        //for example, if skilling progress interface is open, return a randomized value to keep waiting.
        if (Interfaces.isOpen(1251))
            return random.nextLong(250,1500);
        //if our inventory is full, lets bank.
        if (Backpack.isFull()) {
            println("Going to banking state!");
            botState = BotState.BANKING;
            return random.nextLong(250,1500);
        }
        //click my tree, mine my rock, etc...
        SceneObject tree = SceneObjectQuery.newQuery().name("Tree").option("Chop").results().nearest();
        if (tree != null) {
            println("Interacted tree: " + tree.interact("Chop"));
        }
        return random.nextLong(1500,3000);
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }
}
