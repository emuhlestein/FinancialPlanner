# Retirement Helper
## Description
The app is intended to help people plan for their retirements. It will help them track their monthly expenses and the amount of income they can expect to receive in retirement. These will be tracked over time and the user can extrapolate out in the future to see when they can retire, that is when their projected retirement income will be more than their expenses. The user can set milestones. These are important ages. This will allow the user to see how much retire income they can receive at various ages. This will help them identify the age at which they can reasonably retire.

## Features
*	Estimate Social Security benefits.
*	Estimate spousal benefits.
*	Estimate taxes during retirement.
*	Track other sources of income: savings, 401(k), investments, …
*	Track monthly expenses, actual and retirement-only expenses.
*	All income and expenses will be tracked in an Sqlite database.
*	Extrapolate expenses and income to project a retirement date.
*	Show mile stones: key mile stones in US: age 59 ½, 62, full retirement age, …
*	Track personal data like birthday so mile stones can be determined.
*	Graphical show monthly expenses and income.
*	Graphical show when a person can expect to retire.
*	Firebase will be backend so information can be saved and shared.

## Getting Started
### Register
To get started, a user will need to register. An email and password are required. This will allow the data to be stored using Firebase. 

### Personal Information
Next the will enter name and birthdate. Sensitive information like Social Security number is not stored. The birthday will allow the app to estimate what Social Security payments will be, the date at which pensions begin and what income can be expected at a certain age.

### Set up Expense Categories
The user will then set up exepense categories so they can track their expenses based on these categories. The categories can be whatever the user desires. For example, Rent, Mortgage, Healthcare, Groceries, Utilities and so on. Expeneses are tracked on a monthly basis.

### Set up Income Sources
The user will then set up income sources like savings/investing accounts, pensions, government-sponsored pensions like Social Security in the US.

### Establish Milestones
The user can now establish milestones. These are ages of interest to the user. This will allow the user to see what income the can expect to see at a given age. It will also estimate their expneses at the given age. Based on income growth and reduction in expenses, a retirement age can be estimated.

### Screens

## Third Party Libs Used
### Firebase
For persisting data to cloud for purpose of sharing.

### Butterknife
For making UI code easier.

### MPAndroidChart
For creating graphs.

## Development Environment
Android Studio

