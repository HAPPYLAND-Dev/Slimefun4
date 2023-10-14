package city.norain.slimefun4.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SlimefunInventoryHolder implements InventoryHolder {
    protected Inventory inventory;

    protected void setInventory(Inventory inv) {
        inventory = inv;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
