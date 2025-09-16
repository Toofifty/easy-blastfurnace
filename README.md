# Easy Blast Furnace

Helps you train at the blast furnace more efficiently

## Features

- Supports multiple different methods
    - Gold bars
    - Silver bars
    - [Steel/Mithril/Adamant/Rune bars](https://oldschool.runescape.wiki/w/Blast_Furnace/Strategies#Bar_patterns)
    - [Gold + metal bar hybrid](https://oldschool.runescape.wiki/w/Blast_Furnace/Strategies#Hybrid_method)
    - Silver + metal bar hybrid
- __Tick Perfect Methods__
  - Optionally enabled in the config settings, aims to remove waiting times for bars.
- Shows next item/object to click
- Shows coal bag contents
- Prompts to drink a stamina dose when required
    - Can be disabled in the plugin settings
    - Can configure to use Super Energy or Energy Potions, or Strange fruit

## Requirements

- Ice gloves or Smiths Gloves (i)

## Optional
- Goldsmith gauntlets or Smithing/Max cape (for gold bar & hybrid methods)
- Coal bag (for metal bar & hybrid methods)
- Stamina/Super Energy/Energy potions or Strange fruit easily accessible in bank

## Getting started

### Recommended plugins

* __Blast Furnace__ (Runelite) - __enable__
    * Disable __Show conveyor belt clickbox__
    * Disable __Show bar dispenser clickbox__
    * Enable __Show coffer time remaining__
* __Menu Entry Swapper__ (Runelite) - __enable__
    * Item Swaps → __Bank Deposit Shift-Click__: Set to __Eat/Wield/Etc.__
        * Allows you to use shift-click to fill your coal bag and drink stamina doses without closing the bank.
    * Item Swaps → __Bank Withdraw Shift-Click__: Set to __Withdraw-All__
        * Optional - you can also just change your default withdraw amount in the bank itself.
* __Bank Tags__ (Runelite) - __enable__
    * Lets you have all your potions, ores, gear etc. showing in the same place at the same time.
* __Inventory Setups__ (Runelite) - __enable__
  * Can be used alongside Bank Tags, allows for more setups for different skilling/bossing activities

### Setup

1. Withdraw an ore to pick a method. If you withdraw both a metal and gold ore, the hybrid method will be selected.
    1. You can reset the method at any time by __shift-right clicking__ the method overlay
2. Deposit the ores.
3. Withdraw/equip required items as prompted by the plugin.
4. Optional - Enable the `Use deposit inventory` option to highlight individual items in your inventory
   1. Right click & lock slot for your coal bag/gloves so you don't deposit those with your bars.