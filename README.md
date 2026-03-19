# Project:  Perfume Shop #



---

## Development Team ##

Business Client:  Farhan Farhan<br/>
Lead Developer:  Fardin Sahriar AL Rafat<br/>
Quality Control:  Chris Hopkins<br/>

---

## Description ##

This project will allow the tracking of sales transactions in the Perfume Shop. This includes the sale of different types of perfumes, as well as the specific bottle sizes. It will also calculate the taxable amount, and provide a subtotal and total amount due.  <br/><br/>

---

## Color ##

Main Color: darkgoldenrod (Primary headers and highlights)<br/>

Secondary Color: darkblue (Navigation and structure)<br/>

Accent Color: darkred (Error messages and alerts)<br/>

---

## Required Fields ##

This will be a list of fields and their datatype (class design format). There are expected to be a minimum of six fields.<br/>
-id: int //primary key<br/>
-transactionDate: String Note: yyyy-MM-dd<br/>
-customerName: String<br/>
-phoneNumber: String<br/>
-perfumeChoice: String<br/>
-perfumeSize: String<br/>
-pricePerBottle: double<br/>
-quantity: int<br/>
-subTotal: double<br/>
-taxAmount: double<br/>
-total: double<br/>

---

## Calculation ##

Once the user has selected their choice of perfume, bottle size, and entered the quantity and price per bottle, we can calculate the total of their order.<br/>
Tax rate is 15%<br/>

Here is the calculation that will be performed for each transaction:<br/>
subTotal = pricePerBottle * quantity<br/>
taxAmount = subTotal * taxRate<br/>
total = subTotal + taxAmount<br/>

Example calculation:<br/>
A customer orders 2 bottles of Sauvage at $140 each.<br/>
subTotal = 140 * 2 = 280<br/>
taxAmount = 280 * 0.15 = 42<br/>
total = 280 + 42 = $322<br/>

---

## Report Details ##

The user enters a perfume name and all transactions containing that specific perfume are shown. The list is displayed in descending order by Order ID (Primary Key).

---