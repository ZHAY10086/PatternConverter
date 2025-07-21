# Pattern Converter

A Minecraft mod that allows you to convert recipe patterns between different mods.

Currently supported are:

- [Refined Storage](https://refinedmods.com/refined-storage/)
- [Applied Energistics 2](https://appliedenergistics.org)
- [Integrated Dynamics](https://integrateddynamics.rubensworks.net)


## How to use

![GUI](https://github.com/davenonymous/patternconverter/blob/1.21.1/.github/converter-gui.png?raw=true)

1. Place the Pattern Converter block somewhere.
2. Insert patterns to be copied into the three slots on the top left.
3. Insert blank patterns into the pattern slot in the middle.
4. Converted patterns will appear in the output slot on the right.
5. The original patterns will appear in the output slot on the bottom left.


## Block Sides

Top:
- Insert patterns to be copied.
- Insert blank patterns to be written to.

Bottom:
- Output slot for converted patterns.
- Output slot for original patterns.

Sides:
- All of the above

If you want to separate the original patterns from the converted patterns, use some logistics mod
that supports filtering e.g. by mod. Or, you know, your storage network's own filtering capabilities.

## Lossy conversion

Not all pattern functionalities can be converted between mods.
In this case, the pattern will be converted as much as possible and the rest will be lost.

A few examples of lossy conversions are:
- Integrated Dynamics patterns support specifying reusable items. AE and RS do not.
- Refined Storage and Applied Energistics patterns support specifying multiple fluids, but Integrated Dynamics does not.
- All mods have varying support for using item and fluid tags. ID allows specifying a single tag,
  while RS allows specifying multiple tags.
