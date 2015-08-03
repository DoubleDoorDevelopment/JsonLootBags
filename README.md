JsonLootBags
============

This mod adds JSON file based lootbags!

How it works
------------

First the easy parts:

- This bag drops it's contents on the player, so you can't exploit it for more inventory space.
- You can't tell what bag you got from looking at the item. (its not determined until you rightclick)
- The color/name of the item is closable per bag type.

Now for the complex bit:

The lootbags pick items to drop the same way a dungeon chest is filled, aka it fakes an inventory to be filled by Minecraft's WeightedRandomChestContent code.

This results in the following parameters:
- The amount of random items to be added to the 'inventory', called amountOfItemsMin & amountOfItemsMax. 
- The amount of slots in the 'inventory', called invSlotsMin & invSlotsMax.
- The array of weighted random items to pick from, with min amount, max amount & weight, called items.

A note on the amount of slots: When filling a chest, Minecraft picks a random slot to put the item in. It does not check to see if the slot is empty!

Json format
-----------

Per lootbag, you make 1 json file. The extension needs to be .json! The name of the file doesn't matter.

***Everything is always CASE SENSITIVE unless specifically stated otherwise.***

### Json template
```javascript
{
  "itemname": String,
  "color": Color Object,
  "invSlotsMin": Integer,
  "invSlotsMax": Integer,
  "amountOfItemsMin": Integer,
  "amountOfItemsMax": Integer,
  "rarity": String,
  "items": [
    Item Objects here
  ]
}
```

For both `invSlots` and `amountOfItems` actual amount is randomly picked between min & max

Property         | Type    | Explanation
-----------------|---------|----------------------------------------------------------
itemname         | String  | The displayname of the itemstack.
color            | Various | See later on in this document.
invSlotsMin      | Integer | Minimum amount of slots in the inventory.
invSlotsMax      | Integer | Maximum amount of slots in the inventory. 
amountOfItemsMin | Integer | Minimum amount of items to be distributed over the slots.
amountOfItemsMax | Integer | Maximum amount of items to be distributed over the slots.
rarity           | String  | Determines formatting of the itemname, see later.
items            | Objects | The itemstack objects, see later.

#### Color

Color can be represented in a couple of ways:
- A number (known as the java color code)
- A hexadecimal number (same as CSS color codes, but the `#` is needs to be replaced by `0x`
- An array of RGB(A) values. Eater 0.0 trough 1.0 or 0 trough 255.
- An object with values r(ed), g(reen), b(lue) and or a(lpha).
- A string withe the standard colors known by java, as seen [here](http://docs.oracle.com/javase/7/docs/api/java/awt/Color.html).

#### Rarity

The following rarities are possible in Minecraft:
- Common: White name, like normal items.
- Uncommon: Yellow name, like enchanted books.
- Rare: Aqua name, like enchanted items.
- Epic: Light purple name, like Notch apple.

#### Item Objects

Fist of all, an example:
```javascript
{
  "min": 1,
  "max": 3,
  "weight": 10,
  "itemstack": {
    "item": "minecraft:stick",
    "stacksize": 1,
    "damage": 0,
    "nbt": 
  }
}
```
Property  | Type    | Explanation
----------|---------|----------------------------------------------------------
min       | Integer | The minimum number of these items.
max       | Integer | The maximum number of these items.
weight    | Integer | How often the item is chosen. (Higher is more)
itemstack | Object  | The itemstack object

##### Itemstack objects

Property  | Type    | Explanation
----------|---------|----------------------------------------------------------
item      | String  | Item name, in `modid:item` format
stacksize | Integer | Actually not used in this case (random value between min &amp; max is used)
damage    | Integer | The damage value / metadata value
nbt       | Object  | Optional, nbt value for the itemstack. You can obtain this trough the jsonlootbags command.
