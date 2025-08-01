package net.martinprobson.basket.domain

case class Item (product: Product, qty: Int) {

  /**
   * Two Items are considered equal if they contain the same {@link Product}
   * @param obj The other object we are comparing
   * @return true if equal, false otherwise
   */
  override def equals(obj: Any): Boolean = obj match {
    case that: Item =>
      this.product == that.product
    case _ => false
  }

  /**
   * hashCode() function to match equals above.
   * @return A hashcode for this instance
   */
  override def hashCode(): Int = {
    // Return the hash code of the 'product' field.
    // The 'qty' field is ignored.
    product.hashCode
  }
}
