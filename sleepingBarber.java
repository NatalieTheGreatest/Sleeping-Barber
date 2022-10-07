//Sleeping barber
//Developed mostly by Natalie Friede 010892127
//By that I mean I used the starter template 
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class sleepingBarber{

    // main is here
    public static void main (String[] args){
    // Input sleepTimeBarber and numChairs from command line
    int sleep = 5;
    int numChairs = 3;
    try{
    sleep = Integer.parseInt(args[0]);
    }
    catch(Exception e){
        //Mostly for if they give random numbers or not enough args. Not a big deal
        System.out.println("Integer args for sleep please");
    }
    try{
        numChairs = Integer.parseInt(args[1]);
    }
    catch(Exception e){
         System.out.println("Integer args for chairs please");
    }
    //Let the guy rest! No 0 sleep and always has a waiting room
    if(sleep <= 0)
        sleep = 5;
    if(numChairs <= 0)
        numChairs = 3;
    System.out.println("Nap time is " + sleep + ", and there are " + numChairs + " chairs");
    // Default sleepTimeBarber = 5, default numChairs = 3
    // Print parameters.
    // instantiate shop here.
    barberShop shop = new barberShop(numChairs, sleep);
    Barber barber = new Barber(shop);
    CustomerGenerator custGen = new CustomerGenerator(shop);
    Thread oneBarber = new Thread(barber);
    Thread multipleCustGen = new Thread(custGen);
    oneBarber.start();
    multipleCustGen.start();
    }
  }
    // Barber object that will become thread.
class Barber implements Runnable {
    barberShop shop;
    // Need access to shop object.
    public Barber(barberShop shop){
    this.shop = shop;
    }
    public void run(){
    //Simulate sleep by putting thread to sleep
    while(true){
    shop.cutHair();

    }
  }
}
    // Customer object that will become thread.
class Customer implements Runnable{
    barberShop shop;
    String customerName;
    // Need access to shop object.
    public Customer(barberShop shop){
    this.shop = shop;
    }
    public void run(){
    customerName = Thread.currentThread().getName();
    goForHairCut();
    }
    public void hairBegone() {
        System.out.println("Customer " + customerName + " gets their hair cut");

    }
    private void goForHairCut(){
    shop.add(this);

    }
}
    // CustomerGenerator that will become thread to start customer
   // threads.
class CustomerGenerator implements Runnable{
    private final static Random generator = new Random();
    barberShop shop;
    // Need access to shop object.
    public CustomerGenerator(barberShop shop){
    this.shop = shop;
    }
  public void run(){
        while(true){
    // Create customers and pass object “shop”
    Customer c = new Customer(shop);
    // Create thread
    Thread cutsomerThread = new Thread(c);
    // start threads
    cutsomerThread.start();
    // sleep random amount of time
    try {
        //This just seemed fast enough, but not excessive
        Thread.sleep(generator.nextInt(7000));
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
   }
  } 
}

class barberShop {
     BlockingQueue<Customer> customerList;
     int chairs = 0;
     int sleepTime = 0;
     Semaphore mutex;
     Semaphore occupiedSeats;
     //The mutexes
     //for barber
     Semaphore barberReady;


     public barberShop(int chairs, int sleepTime)
     {
         this.chairs = chairs;
         occupiedSeats = new Semaphore(chairs);
         mutex = new Semaphore(1);
         barberReady = new Semaphore(1);
         customerList = new ArrayBlockingQueue<Customer>(chairs);
         this.sleepTime = sleepTime;
     }

    public void cutHair(){
    while(true){
        //Waits to check out the customer list
        try{
        mutex.acquire();
        if(customerList.isEmpty())
        {
            System.out.println("The barber is sleeping");
            //He is ready but he's sleeping
            mutex.release();
            barberReady.release();
            Thread.sleep(sleepTime*1000);
        }
        //If he isn't asleep he falls asleep
        //If he was asleep, he sleeps 
        else {
            try {
                System.out.println("The barber starts cutting hair at " + new Date());
                //He's getting ready
                customerList.poll().hairBegone();
                mutex.release();
                Thread.sleep(sleepTime*1000);
                System.out.println("The barber is done cutting hair at " + new Date());
                barberReady.release();
                

            } catch (InterruptedException e) {
                System.out.println("Barber can't catch a break and is insomniac");
            }
        }
        }
        catch(Exception e) {
            System.out.println("Barber can't tell if there's customers because mutex broke");
        }
    }

 }
    // Wait on customer
    // Do things here like update number of customers waiting,
    //signal to wake up barber, etc.
    // Simulate cutting hair with sleep
    
    public void add(Customer customer){
    System.out.println("Customer " + Thread.currentThread().getName() + " enters the shop at " + new Date());

     try{
        //Mutually exclusive access
        mutex.acquire();
        if(customerList.remainingCapacity() > 0)
        {
            occupiedSeats.acquire();
            System.out.println("Customer " + Thread.currentThread().getName() + " gets a chair");
            customerList.add(customer);
            //Done editing stuff someone else can try to enter
            mutex.release();
            try {
           //Waits for barber
           barberReady.acquire();
           occupiedSeats.release();
            }
            catch(Exception e){
                System.out.println("The barber shop had a mixup! Oopsies");
            }
        }
           
        else{
            mutex.release();
            System.out.println("Customer " + Thread.currentThread().getName() + " does not get a seat and leaves the shop"); 
            //I was told not to kill them, so they just kinda hang out and accumulate at this point
            //Probably not ideal, but you can't go killing customers!!
        }
     }
     catch(Exception e)
     {
         System.out.println("Barber shop isn't feeling so good rn try later");
     }

    }
}