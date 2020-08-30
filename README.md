# GdsFlicker

Photo app that allows the user to browse and see Flickr public contents. 

The color scheme has been chosen to ensure the respect of the accessibility guidelines
[the accessibility guidelines](https://material.io/resources/color/#!/?view.left=1&view.right=0&primary.color=6200EE&secondary.color=00a895?raw=false), as defined in material design

The main view is displayed with a gradient background:

![flicker spinner](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_spinner.png?raw=true "Flicker spinner")

The images thumbnails are displayed using a staggered grid layout with 2 columnes in portarit and 3 columns in landscape mode.
A gradient scrim is being used to display the images title independently of their colors.

![flicker main portrait](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_ferrari_search.png?raw=true "flicker main portrait")
![flicker main landscape](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_main_landscape.png?raw=true "flicker main landscape")

The code is written with Kotlin and follows android recommended guidelines. 
It particularly makes the best use of the android jetpack libraries:

**Room** is used to keep the user search queries:

![flicker search](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_search_history.png?raw=true)

**Workmanager** to check if there is new content available. If the app is not on the foreground a notification is sent to the user.

**Pagination** for partial network data loading

**databinding** which simplifies the code greatly

To communicate with Flickr servers, **Retrofit** library has been used. 

Also, the images are downloaded and processed with **Glide**. In the detail view, a blurred, 
stretched version of the orginal image has been used for the background:

![flicker detail portrait](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_detail_portrait.png?raw=true)
![flicker detail landscape](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_detail_landscape.png?raw=true)

If the user wants to access the author page or get more details regarding the cureent image, 
he/she can visualize it inside the app through our loaded webpage

![flicker web page portrait](https://github.com/Chouette2018/GdsFlicker/blob/master/screenshots/flicker_web_page.png?raw=true)
