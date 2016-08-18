JsonLootBags
============

This mod adds JSON file based lootbags!

Since MC 1.9 it uses the new, build in, json based loot table system.

Any `.json` files under `config/JsonLootBags/bags`, including in sub-directories, are added as new lootbag items. (File names don't matter.)

Any `.json` files under `config/JsonLootBags/tables` will be loaded as loot tables in the `jsonlootbags` resource domain. (The path + filename is the table name.) 


Loot tables
-----------

Loot tables can be super complex in 1.9, see the build in ones (can be found in `/assets/minecraft/loot_tables/` for 
vanilla examples.) For more help, use online generators or the Minecraft wiki. If you want to send us a nice 
(preferably vanilla only) example to put here, let us know.

You specify what loot table to use in the `loot-table` field, see below.

Luck
----

If you spawn the itemstack with an NBT data field (float / number type) called `luck` it will be passed on to the loot table.
You can use that to manipulate the drops a within 1 loot table. You are not required to use this, by default luck will be 0.

Json format
-----------

```json
{
    "loot-table": "minecraft:chests/village_blacksmith", // REQUIRED What loot table to use. See above.
    "name": "bag1",                 // REQUIRED Internal item name: alphanumerical and underscores only
    "human-name": "Example bag",    // REQUIRED Human readable name.
    "texture": "jsonlootbags:bag",  // Model file for item. Provide your own via resource pack if desired. Default is "jsonlootbags:bag"
    "rarity": "Epic",   // Color of text. (Common, Uncommon, Rare, or Epic) "Common" is default.
    "effect": true,     // Give item the 'enchanted' effect. Defaults to true if rarity is not common.
    "colors": [         // Colors to use on texture layers. Allows you to ship 1 model/texture and have multiple visuals.
    "0x90C3D4",         // Every line is a number, but you can also encode it in a string and use the more common # or 0x notation.
    "#DB32DB"           // Do not prefix the number with a 0. It will be interpreted as octal based!
                        // If you use "0xFFFFFF" (white), it will have no effect. Use this if you want to skip a layer.
    ]
}
```

