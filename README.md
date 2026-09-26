# Smart Pantry Manager 

Smart Pantry Manager is a java application built for Android devices. It allows you to input your pantry ingredients and it will show you recipes you can make with those ingredients.

This application was built for the Mobile App Development 700 Practical Assignment.

## Features 

* Input pantry ingredients 
* See your saved pantry ingredients 
* Update a pantry ingredient 
* Delete a pantry ingredient 
* Allow quantity and unit for each ingredient 
* Optional expiry date 
* Check what recipes you can make with your pantry ingredients
* See suggested recipes 
* See ingredients and method for recipe 
* Allow navigation to Pantry, Recipes and Settings page
* Save settings 

## CRUD 

The application allows you to Create, Read, Update and Delete pantry ingredients.

* Create - Add a new pantry ingredient 
* Read - See your pantry ingredients 
* Update - Update a pantry ingredient 
* Delete - Delete a pantry ingredient 

## Database 

A Sqlite database is used through `SQLiteOpenHelper`. 

There are 3 tables within the database. 

* `pantry_items` This holds all of the user's pantry ingredients.
* `recipes` Holds the recipes that are available. 
* `recipe_ingredients` Holds all of the ingredients needed to complete each recipe.

Data will remain in the database once the application is closed and reopened.

## Recipe Suggestions 

It will look at your pantry items and see if there are any recipes that you can make.

If your pantry items have enough of each ingredient that the recipe calls for then that recipe will be displayed on the suggested recipes page.

Some unit conversions are used such as kg to g and l to ml.

## Languages 

Java, Android Studio, XML, SQLite, RecyclerView, SharedPreferences, Git/GitHub

## Application Pages 

There are 5 pages that you can navigate to within the application.

1. Pantry page will show you your saved ingredients.
2. Add ingredient / edit ingredient page will allow you to add a new ingredient or update an existing one.
3. Suggested recipes page will display recipes that you can make using your pantry items.
4. Recipe page will show you what ingredients are needed and the method to follow.
5. Settings page will hold your settings. 

## How To Run Application 

1. Clone or download this repository. 
2. Open in Android Studio. 
3. Let gradle build. 
4. Start an Android Emulator or connect your Android Device.
5. Make sure you have `app` selected. 
6. Hit run on Android Studio. 

## Git 

I used git and github to keep track of my work while developing this application.

You can see my commits on this repository to see how I went about creating and updating this project.

## Author 

Karen 