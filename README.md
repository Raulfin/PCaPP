PCaPP
=====

XML Repository for [PerMa Compatibility and PaMa Patches (PCaPP)](http://www.nexusmods.com/skyrim/mods/59257) and [Perkus Maxamus](http://www.nexusmods.com/skyrim/mods/59849).

Mini Releases can be found on the [Releases](https://github.com/Raulfin/PCaPP/releases) page. This will be updated nightly between main updates on the PCaPP page.

The XMLs are psudo-patches to create compatibility with Perkus Maxamus (PerMa) using PatchusMaxamus (PaMa), the Java patcher included with PerMa. They will adjust Armor Rating, Damage Rating, Weapon Type, create Enchanted variants based on the Enchanting.xml and Distribute items based on the LeveledLists.xml.

Any item data added to the XML should match the original item data in the originating esp. If a mod adds a weapon that is Daedric material it should be listed as Daedric in the XML. Weapon Type should also be true to the esp, there is some room with Sword, Greatsword, Waraxe and Battleaxe as PerMa adds several weapon type based on these, a Sword can be either an Arming Sword, Broadsword or a Long Sword, in these situations the Type should match the visual.

Some mods will have an optional file to change armors from Heavy to Light or vice versa, for these the Main version should be the one covered, unless the item names are different. Such as "Cool Armor" and "Cool Armor (Light)" or "Cool Light Armor". 

Don't worry about editing any of the material or type data, that all falls under balance, there is a topic on the PCaPP page to discuss [balance](http://www.nexusmods.com/skyrim/mods/59257/?tab=5&&navtag=http%3A%2F%2Fwww.nexusmods.com%2Fskyrim%2Fajax%2Fcomments%2F%3Fmod_id%3D59257%26page%3D1%26sort%3DDESC%26pid%3D0%26thread_id%3D2340894&pUp=1).

When adding code, please avoid using spaces, use tabs instead, tabs help keep the file size a lot smaller than spaces and keep everything more uniform.

The [PCaPP Wiki](https://github.com/Raulfin/PCaPP/wiki) contains a lot of information on adding support for mods, give it a read if interested in helping add support for mods.
