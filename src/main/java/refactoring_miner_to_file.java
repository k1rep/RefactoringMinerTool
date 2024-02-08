import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import org.eclipse.jgit.lib.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.io.*;

public class refactoring_miner_to_file {
    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        // 信息字典
        // 'teiid': https://github.com/teiid/teiid, master
        // 'wildfly': https://github.com/wildfly/wildfly, main
        // 'drools': https://github.com/kiegroup/drools, main
        // 'weld': https://github.com/weld/core, master
        // 'infinispan': https://github.com/infinispan/infinispan, main
        String proj_name = "WildFlyElytron";
        String github_link = "https://github.com/wildfly-security/wildfly-elytron";
        String brench = "master";

        // 下载项目到本地仓库
        Repository repo = gitService.cloneIfNotExists(
                "tmp/" + proj_name,
                github_link);

        // 挖掘并筛选 refactorings
        try {
            miner.detectAll(repo, brench, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter("refactoring_files/" + proj_name + "/" + commitId + ".txt"));

                        System.out.println("【" + get_current_time() + "】Refactorings at " + commitId);
                        for (Refactoring ref : refactorings) {
                            out.write(ref.toString() + "\n##########\n");
                        }
                        out.close();
                        System.out.println("【" + get_current_time() + "】" + commitId + "导出成功！");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void handleException(String commitId, Exception e) {
                    System.err.println("Error processing commit " + commitId + ": " + e.getMessage());
                    e.printStackTrace();
                    // 捕获空指针异常并跳过出现问题的提交
                    if (e instanceof NullPointerException) {
                        System.err.println("Skipping problematic commit " + commitId);
                    }
                }
            });
        }catch (Exception e){
            System.out.println("【" + get_current_time() + "】" + "出现异常！");
            e.printStackTrace();
        }
    }

    public static String get_current_time(){
        // 创建一个Date对象，表示当前时间
        Date currentDate = new Date();

        // 创建SimpleDateFormat对象，指定时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 使用SimpleDateFormat格式化当前时间并输出
        return dateFormat.format(currentDate);
    }
}