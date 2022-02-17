## Common Package

This package comprises two classes:
### (a). CacheManager
It's roles include:

1. Holding for us/Caching for us simple variables that we'll need when constructing our application. We make
them static so that we don't tie them down to an instance of any class. Thus we are able to access them
globally from any class without having to import and instantiate any given class.


(b). Constants
This class's roles include:
1. Holding for us all variables whose values won't change during the application lifetime. Instead of
defining them everywhere, we will hold them under one roof.

