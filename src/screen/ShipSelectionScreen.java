package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.Core;
import entity.Entity;
import entity.Ship;

public class ShipSelectionScreen extends Screen {

    private static final int SELECTION_TIME = 200;
    private final Cooldown selectionCooldown;
    private int selectedShipIndex = 0; // 0: NORMAL, 1: BIG_SHOT, 2: DOUBLE_SHOT, 3: MOVE_FAST
    private final Ship[] shipExamples = new Ship[4];

    private boolean backSelected = false; // If current state is on the back button, can't select ship

    public ShipSelectionScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        shipExamples[0] = new Ship(width / 2 - 100, height / 2, Entity.Team.PLAYER, Ship.ShipType.NORMAL, null);
        shipExamples[1] = new Ship(width / 2 - 35, height / 2, Entity.Team.PLAYER, Ship.ShipType.BIG_SHOT, null);
        shipExamples[2] = new Ship(width / 2 + 35, height / 2, Entity.Team.PLAYER, Ship.ShipType.DOUBLE_SHOT, null);
        shipExamples[3] = new Ship(width / 2 + 100, height / 2, Entity.Team.PLAYER, Ship.ShipType.MOVE_FAST, null);
    }

    /**
     * Returns the selected ship type to Core.
     *
     * @return The selected ShipType enum.
     */
    public Ship.ShipType getSelectedShipType() {
        return switch (this.selectedShipIndex) {
            case 1 -> Ship.ShipType.BIG_SHOT;
            case 2 -> Ship.ShipType.DOUBLE_SHOT;
            case 3 -> Ship.ShipType.MOVE_FAST;
            default -> Ship.ShipType.NORMAL;
        };
    }

    public final int run() {
        super.run();
        return this.returnCode;
    }

    protected final void update() {
        super.update();
        draw();
        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP) || inputManager.isKeyDown(KeyEvent.VK_W)) {
                backSelected = true;
                selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN) ||  inputManager.isKeyDown(KeyEvent.VK_S)) {
                backSelected = false;
                selectionCooldown.reset();
            }
            if (!backSelected) {
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
                    this.selectedShipIndex = this.selectedShipIndex - 1;
                    if (this.selectedShipIndex < 0) {
                        this.selectedShipIndex += 4;
                    }
                    this.selectedShipIndex = this.selectedShipIndex % 4;
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D)) {
                    this.selectedShipIndex = (this.selectedShipIndex + 1) % 4;
                    this.selectionCooldown.reset();
                }
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                this.returnCode = backSelected ? 5 : 6;
                this.isRunning = false;
            }
            int mx = inputManager.getMouseX();
            int my = inputManager.getMouseY();
            boolean clicked = inputManager.isMouseClicked();

            java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);

            if (clicked && backBox.contains(mx, my)) {
                this.returnCode = 5;
                this.isRunning = false;

            }
        }
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawShipSelectionMenu(this, shipExamples, this.selectedShipIndex);

        // hover highlight
        int mx = inputManager.getMouseX();
        int my = inputManager.getMouseY();
        java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);
        boolean backHover = backBox.contains(mx, my);
        drawManager.drawBackButton(this, backHover || backSelected);

        drawManager.completeDrawing(this);
    }
}