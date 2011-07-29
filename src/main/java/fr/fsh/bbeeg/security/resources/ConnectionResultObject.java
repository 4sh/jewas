package fr.fsh.bbeeg.security.resources;

/**
 * @author driccio
 */
public class ConnectionResultObject<T> {
    public static enum ConnectionStatus {
        SUCCESS("success"), FAILURE("failure");

        private String status;

        private ConnectionStatus(String status) {
            this.status = status;
        }

        public String status() {
            return status;
        }
    }

    public static class SuccessObject {
        private String url;

        public SuccessObject url(String _url){
            this.url = _url;
            return this;
        }

        public String url(){
            return this.url;
        }
    }

    public static class FailureObject {
        private String msg;

        public FailureObject msg(String _msg){
            this.msg = _msg;
            return this;
        }

        public String msg(){
            return this.msg;
        }
    }

    private ConnectionStatus status;
    private T object;

    public ConnectionResultObject status(ConnectionStatus _status){
        this.status = _status;
        return this;
    }

    public ConnectionStatus status(){
        return this.status;
    }

    public ConnectionResultObject object(T _object){
        this.object = _object;
        return this;
    }

    public T object(){
        return this.object;
    }
}
