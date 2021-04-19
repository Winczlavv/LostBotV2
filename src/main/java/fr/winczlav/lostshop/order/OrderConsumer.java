package fr.winczlav.lostshop.order;

@FunctionalInterface
public interface OrderConsumer<B,C,D,E> {
    /**
     * @param b {@link OrderData}
     * @param c {@link OrderData}
     * @param d {@link OrderData}
     * @param e {@link OrderData}
     * @return
     */
   void accept(B b, C c, D d, E e);
}
