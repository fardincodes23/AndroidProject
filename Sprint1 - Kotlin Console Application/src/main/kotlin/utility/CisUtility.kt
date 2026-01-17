package utility

import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.JOptionPane

/**
 * Class containing utility methods that are useful for our projects.
 *
 * @author bjmaclean
 * @since 20181115
 */
object CisUtility {
    private val input = Scanner(System.`in`)

    //The isGUI will be used to determine if JOptionPane is used or console
    private var isGUI = false

    fun setIsGUI(isGUI: Boolean) {
        CisUtility.isGUI = isGUI
    }

    /**
     * Return the default currency String value of the double passed in as a
     * parameter.
     *
     * @param inputDouble double to be formatted
     * @return String in default currency format
     *
     * @since 20211020
     * @author BJM
     */
    fun toCurrency(inputDouble: Double?): String {
        val formatter = NumberFormat.getCurrencyInstance()
        return formatter.format(inputDouble)
    }

    /*
    Methods to get input from the user
     */
    /**
     * Method which will prompt the user and return the value entered as a
     * String.
     *
     * @author BJ MacLean
     * @param prompt The user prompt
     * @return The String entered by the user
     * @since 20181121
     */
    fun getInputString(prompt: String?): String {
        val userOption: String

        if (isGUI) {
            userOption = JOptionPane.showInputDialog(prompt)
        } else {
            println(prompt)
            userOption = input.nextLine()
        }

        return userOption
    }

    /**
     * Method which will prompt the user and return an int value.
     *
     * @author BJ MacLean
     * @param prompt The user prompt
     * @return The number entered by the user
     * @since 20181121
     */
    fun getInputInt(prompt: String?): Int {
        val enteredString = getInputString(prompt)
        val entered = enteredString.toInt()
        return entered
    }

    /**
     * Method which will prompt the user and return a double value.
     *
     * @author BJ MacLean
     * @param prompt The user prompt
     * @return The number entered by the user
     * @since 20181121
     */
    fun getInputDouble(prompt: String?): Double {
        val enteredString = getInputString(prompt)
        val entered = enteredString.toDouble()
        return entered
    }

    /**
     * Method to input a boolean value.The prompt will have y/n instructions
     * appended to it.
     *
     * @author BJ MacLean
     * @param prompt Base prompt for the user
     * @return true/false
     * @since 20200129
     */
    fun getInputBoolean(prompt: String): Boolean {
        val temp = getInputString("$prompt (y/n)")
        return (temp.equals("y", ignoreCase = true) || temp.equals("yes", ignoreCase = true) || temp.equals(
            "true",
            ignoreCase = true
        )
                || temp.equals("1", ignoreCase = true))
    }

    /**
     * Method to display a string for the user
     *
     * @param output The string that will be displayed to the user
     * @since 20181115
     * @author BJM
     */
    fun display(output: String?) {
        //System.out.println(output);
        if (isGUI) {
            JOptionPane.showMessageDialog(null, output)
        } else {
            println(output)
        }
    }

    /**
     * This method will use the Math class to get a random number between 1 and
     * the max inclusive.
     *
     * @author BJ MacLean
     * @param max The upper limit for the random number (inclusive)
     * @since 20181121
     */
    fun getRandom(max: Int): Int {
        //Math.random will give a fraction between 0 

        val theFraction = Math.random()
        val theResult = (theFraction * max).toInt()
        return 1 + theResult
    }

    val random: String
        /**
         *
         * @author BJ MacLean
         * @since 2020
         */
        get() {
            val theClass: Array<Array<String?>>

            var name: String? = null
            val NUMBER_OF_ROWS = 4
            val NUMBER_OF_SEATS_PER_ROW = 6

            val section = getInputString("A or B section?")

            theClass = Array(NUMBER_OF_ROWS) { arrayOfNulls(NUMBER_OF_SEATS_PER_ROW) }

            if (section.equals("B", ignoreCase = true)) {
                theClass[0][0] = "Coffee Break"
                theClass[0][1] = ""
                theClass[0][2] = "Brody"
                theClass[0][3] = "Ryan"
                theClass[0][4] = "Khari"
                theClass[0][5] = ""
                theClass[1][0] = "Bruce"
                theClass[1][1] = ""
                theClass[1][2] = "Cameron"
                theClass[1][3] = "Cole"
                theClass[1][4] = "Neil"
                theClass[1][5] = ""
                theClass[2][0] = "Jems"
                theClass[2][1] = ""
                theClass[2][2] = "Mohammad"
                theClass[2][3] = ""
                theClass[2][4] = "Domanic"
                theClass[2][5] = "Karen"
                theClass[3][0] = "BJ"
                theClass[3][1] = ""
                theClass[3][2] = ""
                theClass[3][3] = ""
                theClass[3][4] = ""
                theClass[3][5] = ""
            } else {
                if (section.equals("a", ignoreCase = true)) {
                    theClass[0][0] = ""
                    theClass[0][1] = "Thomas"
                    theClass[0][2] = "Max"
                    theClass[0][3] = "Marc"
                    theClass[0][4] = "Brandon"
                    theClass[0][5] = "Alex"
                    theClass[1][0] = "Elizabeth"
                    theClass[1][1] = "Nathan"
                    theClass[1][2] = "Ahsun"
                    theClass[1][3] = "Kahla"
                    theClass[1][4] = "Philip"
                    theClass[1][5] = "Logan"
                    theClass[2][0] = "Devon"
                    theClass[2][1] = "Katie"
                    theClass[2][2] = "Kelsie"
                    theClass[2][3] = "Kapil"
                    theClass[2][4] = "Reilly"
                    theClass[2][5] = ""
                    theClass[3][0] = ""
                    theClass[3][1] = ""
                    theClass[3][2] = ""
                    theClass[3][3] = ""
                    theClass[3][4] = "Coffee Break"
                    theClass[3][5] = "BJ"
                }
            }

            do {
                val rowRandom = (Math.random() * NUMBER_OF_ROWS).toInt()
                val seatRandom = (Math.random() * NUMBER_OF_SEATS_PER_ROW).toInt()
                name = theClass[rowRandom][seatRandom]
                //CisUtility.display("Person at the random seat=" + name);
            } while (name == null || name == "")
            display("The winner is=$name")
            return name
        }

    /**
     * Get the formatted date
     * https://stackoverflow.com/questions/1459656/how-to-get-the-current-time-in-yyyy-mm-dd-hhmisec-millisecond-format-in-java
     *
     * @author BJ MacLean
     * @since 20190301
     */
    fun getCurrentDate(format: String?): String {
        //Set the default format.
        var format = format
        if (format == null || format.length == 0) {
            format = "yyyy-MM-dd"
        }

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
    }

    /**
     * Get the formatted date adjusted based on offset
     * https://stackoverflow.com/questions/1459656/how-to-get-the-current-time-in-yyyy-mm-dd-hhmisec-millisecond-format-in-java
     * @param offsetDays Days to offset the date
     * @param format Format for date (default yyyy-MM-dd)
     * @author BJ MacLean
     * @since 20241018
     */
    fun getCurrentDate(offsetDays: Int, format: String?): String {
        //Set the default format.
        var format = format
        if (format == null || format.length == 0) {
            format = "yyyy-MM-dd"
        }

        val theDate = LocalDateTime.now().plusDays(offsetDays.toLong())
        return theDate.format(DateTimeFormatter.ofPattern(format))
    }


    val nowMillis: Long
        /**
         * This method will return the current time in milliseconds since a specific
         * start of time.
         *
         * https://www.tutorialspoint.com/java/lang/system_currenttimemillis.htm
         *
         * @author BJ MacLean
         * @since 20200127
         */
        get() = System.currentTimeMillis()
}
