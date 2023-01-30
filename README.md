# Kachelleger (Tiler)

A JavaFX program to support the user in creating a tile-based map, for example for a board game. The program will randomly place tiles from a given set and determine which tiles fit together automatically.

## Features

- Load a set of tiles at program start
- Determine which tiles fit together automatically
- Display a rectangular grid of spaces
- Randomize a chosen space with a random tile from the set by left-clicking with the mouse
- Clear a chosen space by right-clicking with the mouse
- Expand the grid of spaces by pressing the Spacebar
- Clear all spaces by pressing the Escape key
- Invalid spaces, which cannot be randomized because no tiles from the set fit with their neighbors, will be marked as invalid and displayed differently
- Tile data is modeled as Images in JavaFX
- Access individual pixels of an image using a PixelReader
- The grid of spaces can be implemented as either a GridPane or a Canvas
- A set of tiles that fit well together with a depth of 2 will be provided through Uni2work
   
✨ Encouraged to design your own tile sets! ✨

## Usage

1. Run the program
2. Load a set of tiles at program start
3. Use left-click with the mouse to randomize a chosen space with a random tile from the set
4. Use right-click with the mouse to clear a chosen space
5. Press the Spacebar to expand the grid of spaces
6. Press the Escape key to clear all spaces.

## Notes

Tiles are always square and large

PixelReader is used to access individual pixels of an image

## Example

```ruby
Image example = new Image("file:Example.png");
PixelReader reader = example.getPixelReader();
Color pixelColor = reader.getColor(7, 23);
```

In the example, pixelColor represents the color of the pixel at coordinate (7, 23), the 8th from the left and 24th from the top (the top left pixel has coordinate (0, 0)).
