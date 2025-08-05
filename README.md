
Shopping Basket Assignment
==============================

# Running the code

## Prerequisites
1. [sbt](https://www.scala-sbt.org/download) is the required build tool.
2. Java >=17.

## Installation/ Running the code
1. Clone this repository.
2. Start a sbt shell in the root of this repository.
    * Use `test` to run all the unit tests.
    * Use `run <args>` to run the code from sbt shell, e.g. : -
```
         sbt:shopping-basket> run PriceBasket Apples Milk Bread
         [info] running net.martinprobson.basket.Main PriceBasket Apples Milk Bread
         Subtotal: £3.10
         Apples 10% off: 10p
         Total price: £3.00

         [success] Total time: 0 s, completed Aug 4, 2025, 3:04:14 PM
```
3. Alternatively, a fat jar can be generated using the sbt `assembly` command and the code run from the command line using: -
```
   cd target/scala-3.3.6
   java -jar shopping-basket-assembly-0.0.1-SNAPSHOT.jar PriceBasket Apples Milk Bread
   Subtotal: £3.10
   Apples 10% off: 10p
   Total price: £3.00
```
4. The code uses [logback](https://logback.qos.ch/) logging with root level set to `WARN` for main code and `TRACE` for tests.

# Problem Description
Write a program driven by unit tests that can price a basket of goods taking into account some special offers. 
The goods that can be purchased, together with their normal prices are:

| Product | Cost               |
|---------|--------------------|
| Soup    | 65p per tin        |
| Bread   | 80p per loaf       |
| Milk    | £1.30 per bottle   |
| Apples  | £1.00 per bag      |

The program should accept a list of items in the basket and output the subtotal, the special offer discounts and
the final price.

Input should be via the command line in the form `PriceBasket item1 item2 item3....`

## Current Special Offers
* Apples have a 10% discount off their normal price this week.
* Buy 2 tins of soup and get a loaf of bread for half price.

## For Example 

`PriceBasket Apples Milk Bread`

should output: -
```
Subtotal: £3.10
Apples 10% off: 10p
Total price: £3.00
```

If no special offers are applicable the code should output:
```
Subtotal: £1.30
(No offers available)
Total price: £1.30
```
# Assumptions
* Although the specification does not make this clear, I have assumed the discounts are not cumulative, i.e. 4 tins of soup still means only one loaf of bread at half-price (not two).
* An empty shopping basket (e.g. `PriceBasket `) is not an error.
* I have assumed that a shopping basket never gets too big to be read into memory all at once and then aggregated. 


# Program Structure
The code has been split into packages according to functionality: -

| Package    | Purpose                                                                                                                                                                                                                                                                                      |
|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| domain     | Domain objects                                                                                                                                                                                                                                                                               |
| input      | Processing and validating the input. Converts the input into a `Set[Item]`, [Item](src/main/scala/net/martinprobson/basket/domain/Item.scala) is a [Product](src/main/scala/net/martinprobson/basket/domain/Product.scala) together with a Qty.                                              |
| repository | Responsible for fetching the products and [DiscountRule](src/main/scala/net/martinprobson/basket/domain/DiscountRule.scala)'s                                                                                                                                                                |
| output     | Responsible for formatting the [PriceBasketResult](src/main/scala/net/martinprobson/basket/domain/PriceBasketResult.scala) to a string ready for output                                                                                                                                      |
| pricing    | The main pricing logic that accepts a aggregated shopping basket (`Set[Item]`) and a list of [DiscountRule](src/main/scala/net/martinprobson/basket/domain/DiscountRule.scala)'s and generates a [PriceBasketResult](src/main/scala/net/martinprobson/basket/domain/PriceBasketResult.scala) |

#### Package - domain

| Trait/class               | Description                                                                                                                                     | ImplementedBy                                           | Description                                                                                                                                                         |
|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Discount (class)          | class holding an applied discount (description and amount)                                                                                      | N/A                                                     |                                                                                                                                                                     |
| DiscountRule (trait)      | A discount rule (with an applyDiscount abstract method to apply a discount to a shopping basket)                                                | PercentageDiscount / ConditionalDiscount                | PercentageDiscount - Apply a fixed percentage discount (i.e. Apples 10% off), ConditionalDiscount - Apply a discount based on a condition (dependent product count) |
| Error (trait)             | All errors from the code inherit from this trait                                                                                                | EmptyInputError, InvalidFirstEntry, InvalidProductError |                                                                                                                                                                     |
| Item (class)              | An aggregated shopping basket item (Product and Qty)                                                                                            | N/A                                                     |                                                                                                                                                                     |
| Product (class)           | A Product as returned by the ProductRepository (name and price)                                                                                 | N/A                                                     |                                                                                                                                                                     |
| PriceBasketResult (class) | The result of pricing a shopping basket - contains subtotal before discounts, a list of discounts applied and the total for the shopping basket | N/A                                                     |                                                                                                                                                                     |                                                                                                                                                       |

#### Package - input

| Trait          | Description                                                                                                                       | ImplementedBy      | Description                                 |
|----------------|-----------------------------------------------------------------------------------------------------------------------------------|--------------------|---------------------------------------------|
| InputSource    | source for shopping basket input                                                                                                  | CmdLineInputSource | Fetch shopping basket from the command line |
| InputProcessor | validate and aggregate the shopping basket items into a list of [Item](src/main/scala/net/martinprobson/basket/domain/Item.scala) | InputProcessorImpl |                                             |

#### Package - repository

| Trait                  | Description                   | ImplementedBy                  | Description                                      |
|------------------------|-------------------------------|--------------------------------|--------------------------------------------------|
| DiscountRuleRepository | repository for discount rules | InMemoryDiscountRuleRepository | Hard coded list of discount rules for simplicity |
| ProductRepository      | repository for products       | InMemoryProductRepository      | Hard coded list of products for simplicity       |

#### Package - output

| Trait             | Description                                                                              | ImplementedBy        | Description                                 |
|-------------------|------------------------------------------------------------------------------------------|----------------------|---------------------------------------------|
| OutputProcessor   | Responsible for formatting a PriceBasketResult to a String in the correct output format. | OutputProcessorImpl  | Implementation of an OutputProcessor        |
| CurrencyFormatter | Responsible for formatting output currencies                                             | GbpCurrencyFormatter | Implementation of CurrencyFormatter for GBP | 

#### Package - pricing

| Trait         | Description                                                                               | ImplementedBy     | Description                       |
|---------------|-------------------------------------------------------------------------------------------|-------------------|-----------------------------------|
| PricingEngine | Accepts a set of basket items and a list of discount rules, generates a PriceBasketResult | PricingEngineImpl | Implementation of a PricingEngine |


## Scalability
Scalability of this solution covers a number of different areas: -

1. **Size of individual shopping basket** - The code assumes that an individual shopping never gets so large that it will not all fit into memory. If this is not the case, then some pre-aggregation step may be required.
2. **Number of products/discounts** - Both of these entities come from the repository implementation. Performance here will be dependent on the underlying database implementation.
3. **Number of requests to process a shopping basket** - This can be made horizontally scalable (by putting the pricing engine implementation behind a http endpoint and running via k8s or a load balancer for example).

I'm happy to discuss all the options above in more detail if required.

## Dependency Injection
To keep things simple, I have wired up the dependencies manually in the code (via the companion object of each trait). For real code I would probably replace this with
a DI framework such as [Macwire](https://github.com/softwaremill/macwire) or [Google Guice](https://github.com/google/guice).
