import java.util.concurrent.*;

public class timeout_demo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Void> task = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // 在这里编写需要执行的函数
                // 如果执行时间超过10秒，就跳过它
                System.out.println("执行函数");

                // 模拟长时间执行
                Thread.sleep(15000);

                return null;
            }
        };

        try {
            Future<Void> future = executor.submit(task);
            future.get(10, TimeUnit.SECONDS); // 设置超时时间为10秒
        } catch (TimeoutException e) {
            // 超时异常处理
            System.out.println("函数执行超时，跳过");
        } catch (Exception e) {
            // 其他异常处理
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }
}