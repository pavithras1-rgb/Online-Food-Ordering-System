# QuickBite – Food Ordering System (Java)

## Description
A Java desktop food-ordering mini-project. Users can browse food, add items to a cart, remove selected items, enter delivery details, and save demo orders to SQLite.

## Problem statement and objectives
Manual ordering can make menu browsing, total calculation and order record keeping inconvenient. This project demonstrates a simple digital ordering workflow.

## Features/modules
- Menu with six food items, categories and prices
- Add items to cart and remove selected items
- Automatic total calculation
- Name, phone and address validation
- Order confirmation with generated order number
- SQLite database storage

## Technology stack
Java 17, Java Swing, JDBC, SQLite, Maven, Git and GitHub.

## Prerequisites and installation
Install JDK 17+ and Apache Maven. In VS Code, install Microsoft's **Extension Pack for Java**. Open a terminal in this folder and run `mvn clean compile`. Maven downloads the SQLite JDBC library on the first run.

## Run the application
```bash
mvn clean compile exec:java
```
The application creates `food_ordering.db` in the project folder automatically.

## Sample input and output
Customer: Anu; phone: 9876543210; address: 12 Main Road, Coimbatore. Cart: one Margherita Pizza (₹249) and two Masala Dosa (₹90 each). Expected total: ₹429 and an order confirmation dialog with an order number.

## Testing checklist
Run each test and record the actual result before submission:
| Test | Expected result |
|---|---|
| Launch application | Menu and cart appear |
| Add food | Item appears in cart |
| Add same food again | Quantity and total increase |
| Remove selected item | Item is removed |
| Valid checkout | Confirmation appears and order is stored |
| Empty cart | Validation message |
| Invalid phone | Validation message |
| Incomplete address | Validation message |

Do not mark tests passed until you have run them.

## Team details and contributions
Fill in your actual name, register number and genuine contribution(s) before submission.

## Limitations and future enhancements
This is a local academic demo, not a live delivery service. It has no login, payment gateway or delivery tracking. Future work: admin menu management, customer accounts, order tracking, secure payment and deployment.

## Project structure
```text
Food-Ordering-System-Java/
├── pom.xml
├── README.md
├── .gitignore
├── src/main/java/com/quickbite/FoodOrderingApp.java
└── screenshots/
```

## GitHub workflow
```bash
git init
git add README.md pom.xml .gitignore
git commit -m "Add Java project setup and documentation"
git add src/
git commit -m "Implement food menu and ordering interface"
git checkout -b feature-cart-validation
# Make and test a small improvement on this branch
git add .
git commit -m "Improve cart and checkout validation"
git checkout main
git merge feature-cart-validation
git remote add origin YOUR_GITHUB_REPOSITORY_URL
git push -u origin main
git push -u origin feature-cart-validation
```
If your branch is named `master`, run `git branch -M main`. Capture screenshots of the running app, repository files, meaningful commits and feature-branch merge. Final PDF name: `RegisterNumber_Assignment2_MiniProject.pdf`.
