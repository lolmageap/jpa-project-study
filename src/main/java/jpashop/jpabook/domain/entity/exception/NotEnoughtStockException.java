package jpashop.jpabook.domain.entity.exception;

public class NotEnoughtStockException extends RuntimeException{

    public NotEnoughtStockException() {
        super();
    }

    public NotEnoughtStockException(String message) {
        super(message);
    }

    public NotEnoughtStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughtStockException(Throwable cause) {
        super(cause);
    }

}
