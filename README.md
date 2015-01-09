PCaPP
=====

XML Repository for PerMa Compatibility and PaMa Patches (PCaPP).

The XMLs are psudo-patches to create compatibility with Perkus Maxamus (PerMa) using PatchusMaxamus (PaMa), the Java patcher included with PerMa. They will adjust Armor Rating, Damage Rating, Weapon Type, create Enchanted variants based on the Enchanting.xml and Distribute items based on the LeveledLists.xml.

Any item data added to the XML should match the original item data in the originating esp. If a mod adds a weapon that is Daedric material it should be listed as Daedric in the XML. Weapon Type should also be true to the esp, there is some room with Sword, Greatsword, Waraxe and Battleaxe as PerMa adds several weapon type based on these, a Sword can be either an Arming Sword, Broadsword or a Long Sword, in these situations the Type should match the visual.

Some mods will have an optional file to change armors from Heavy to Light or vice versa, for these the Main version should be the one covered, unless the item names are different. Such as "Cool Armor" and "Cool Armor (Light)" or "Cool Light Armor". 

I am planning to add a few new armor and weapon materials, been saying I would for a while, but haven't yet.

Don't worry about editing any of the material or type data, that all falls under balance, there is a topic on the PCaPP page to discuss balance.

When adding code, please avoid using spaces, use tabs instead, tabs help keep the file size a lot smaller than spaces.
