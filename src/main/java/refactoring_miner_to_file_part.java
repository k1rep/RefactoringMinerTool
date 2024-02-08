import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class refactoring_miner_to_file_part {
    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        // 该项目信息
        String proj_name = "weld";
        String github_link = "https://github.com/weld/core";
        String brench = "master";

        // 下载项目到本地仓库
        Repository repo = gitService.cloneIfNotExists(
                "tmp/" + proj_name,
                github_link);

        // 挖掘并筛选 refactorings
        miner.detectBetweenCommits(repo,
                "b447a885d2aff65e7e95f4d35088f0d0489a3516",
                "a772b9fdc6b6febf6a258c070f1c999124d1522d",
                new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter("refactoring_files/" + proj_name +"/" + commitId + ".txt"));

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
        });
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