import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;


/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & Andrew Fargo
 *
 */
public class AACMappings implements AACPage {
	/** Each category indexed by image location. */
	AssociativeArray<String, AACCategory> mapping;

	/** The home (default) category. */
	AACCategory home;

	/** The current category. */
	AACCategory current;


	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		this.mapping = new AssociativeArray<String, AACCategory>();
		this.home = new AACCategory("");
		this.current = this.home;
		
		File file = new File(filename);
		Scanner scanner;

		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			boolean subitem = line.startsWith(">");
			if (subitem) {
				// Strip the indicator
				line = line.substring(1);
			} else {
				// Go back home
				this.reset();
			}
			int firstSpace = line.indexOf(' ');
			if (firstSpace == -1) {
				// Ignore the line
				continue;
			}
			String imageLoc = line.substring(0, firstSpace);
			String imageText = line.substring(firstSpace + 1);
			this.addItem(imageLoc, imageText);
			if (!subitem) {
				// Enter into that category
				this.select(imageLoc);
			}
		}
		this.reset();
		scanner.close();
	}
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) {
		if (this.getCategory().equals("")) {
			try {
				this.current = this.mapping.get(imageLoc);
			} catch (KeyNotFoundException e) {
				throw new NoSuchElementException();
			}
			return "";
		}
		return this.current.select(imageLoc);
	}
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		return this.current.getImageLocs();
	}
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.current = this.home;
	}
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		File file = new File(filename);
		PrintWriter pen;
		try {
			pen = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		// Save current category, then go home
		AACCategory preCategory = this.current;
		this.reset();
		String[] mainCategoryLocs = this.getImageLocs();

		for (String categoryLoc : mainCategoryLocs) {
			this.select(categoryLoc);
			pen.printf("%s %s\n", categoryLoc, this.getCategory());
			String[] entryLocs = this.getImageLocs();
			for (String entryLoc : entryLocs) {
				pen.printf(">%s %s\n", entryLoc, this.select(entryLoc));
			}
			this.reset();
		}

		pen.close();
		// Restore saved category
		this.current = preCategory;
	}
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		if (this.getCategory().equals("")) {
			try {
				this.home.imageMap.set(imageLoc, text);
				this.mapping.set(imageLoc, new AACCategory(text));
			} catch (NullKeyException e) {
				throw new RuntimeException("Invalid Image Location"); // Panic
			}
		} else {
			this.current.addItem(imageLoc, text);
		}
	}


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return this.current.getCategory();
	}


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.current.hasImage(imageLoc);
	}
}
