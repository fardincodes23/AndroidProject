# Perfume Shop - Kotlin Console Application (Sprint 1)

## 📖 Description
This project is the initial **Sprint 1** release of the "Perfume Shop" capstone project. Currently, it is a **Console-Based Application** written entirely in **Kotlin**.

The application functions as a Point of Sale (POS) system that allows the user to record perfume sales transactions. It utilizes a **Layered Architecture** (Entity, Business Object, Main) to separate data, logic, and user interface, serving as the foundation for the upcoming Android mobile application.

## Development Team
* **Lead Developer:** Fardin Sahriar AL Rafat
* **Business Client:** Farhan Farhan
* **Quality Control:** Chris Hopkins

## 🛠 Tech Stack
* **Language:** Kotlin
* **Environment:** Console (JVM)
* **IDE:** IntelliJ IDEA / Android Studio
* **Architecture:** Layered (Entity, BO, Utility)
* **Data Storage:** In-Memory `ArrayList` (Sprint 1) / MySQL (Planned for future sprints)

## 📊 Data Model (PerfumeTransaction)
The application tracks the following fields for every transaction, mapped strictly to the project requirements:

| Field Name | Data Type | Description |
| :--- | :--- | :--- |
| `id` | `Int` | Unique ID (Auto-generated). |
| `transactionDate` | `String` | Format: `yyyy-MM-dd`. Date of sale. |
| `customerName` | `String` | Name of the purchaser. |
| `phoneNumber` | `String` | Customer contact number. |
| `perfumeChoice` | `String` | The specific brand or scent selected. |
| `perfumeSize` | `String` | Size of the bottle (e.g., 100ml). |
| `pricePerBottle` | `Int` | Cost per individual unit. |
| `quantity` | `Int` | Number of units purchased. |
| `subTotal` | `Double` | **Auto-Calculated:** `price * quantity` |
| `taxAmount` | `Double` | **Auto-Calculated:** `subTotal * Tax Rate` |
| `total` | `Double` | **Auto-Calculated:** `subTotal + taxAmount` |

## 🧮 Business Logic & Calculations
The application uses a specialized **Business Object (BO)** (`PerfumeTransactionBO`) to handle financial logic automatically.

### Formulas
1. **Subtotal:** `quantity * pricePerBottle`
2. **Tax Amount:** `subTotal * 0.15` (15% HST)
3. **Total Price:** `subTotal + taxAmount`

*Note: Financial values are displayed using standard currency formatting (e.g., $140.00).*

## 🚀 Features (Sprint 1)
* **Add Transaction:** User inputs sales data via a guided console menu.
* **Auto-Calculation:** Tax and totals are calculated instantly upon entry.
* **View Ledger:** Displays a formatted list of all recorded transactions in the current session.
* **Utility Integration:** Uses `CisUtility` for robust input handling and currency formatting.
* **Dummy Data:** Includes an `initialize()` function to preload test data for quick demonstration.

## ⚙️ How to Run
1. **Open the Project:** Open the root folder in **IntelliJ IDEA** or **Android Studio**.
2. **Verify Structure:** Ensure the following packages exist:
    * `src/main/kotlin/Main.kt`
    * `src/main/kotlin/entity/PerfumeTransaction.kt`
    * `src/main/kotlin/bo/PerfumeTransactionBO.kt`
    * `src/main/kotlin/utility/CisUtility.kt`
3. **Run:** Right-click `Main.kt` and select **Run 'MainKt'**.
4. **Interact:**
    * Enter **A** to add a sale.
    * Enter **B** to view the ledger.
    * Enter **X** to exit.