This is my implementation of the sleeping barber problem.
The customer generator makes a new customer every ~5 or so seconds at random
The customers enter the store, check if there is space in the waiting room, then sit and wait for the barber
Barber starts out sleeping for whatever the inputted sleep time is
Once he wakes up, he takes the first customer thread and cuts their hair
After that, the customer isn't in the waiting room anymore and the barber is available again
Thread killing is bad, so even if they get dismissed due to lack of room in the waiting area, they just kinda hang out