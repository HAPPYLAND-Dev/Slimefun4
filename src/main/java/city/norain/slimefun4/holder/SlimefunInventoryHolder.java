package city.norain.slimefun4.holder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class SlimefunInventoryHolder implements InventoryHolder {
    @Setter
    protected Inventory inventory;
}
