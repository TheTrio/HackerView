package sample;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        TextArea textArea = new TextArea();
        textArea.setId("HackerBox");
        AtomicInteger i = new AtomicInteger(0);
        String kernel = "struct group_info init_groups = { .usage = ATOMIC_INIT(2) };\n" +
                "\n" +
                "struct group_info *groups_alloc(int gidsetsize){\n" +
                "\n" +
                "\tstruct group_info *group_info;\n" +
                "\n" +
                "\tint nblocks;\n" +
                "\n" +
                "\tint i;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tnblocks = (gidsetsize + NGROUPS_PER_BLOCK - 1) / NGROUPS_PER_BLOCK;\n" +
                "\n" +
                "\t/* Make sure we always allocate at least one indirect block pointer */\n" +
                "\n" +
                "\tnblocks = nblocks ? : 1;\n" +
                "\n" +
                "\tgroup_info = kmalloc(sizeof(*group_info) + nblocks*sizeof(gid_t *), GFP_USER);\n" +
                "\n" +
                "\tif (!group_info)\n" +
                "\n" +
                "\t\treturn NULL;\n" +
                "\n" +
                "\tgroup_info->ngroups = gidsetsize;\n" +
                "\n" +
                "\tgroup_info->nblocks = nblocks;\n" +
                "\n" +
                "\tatomic_set(&group_info->usage, 1);\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tif (gidsetsize <= NGROUPS_SMALL)\n" +
                "\n" +
                "\t\tgroup_info->blocks[0] = group_info->small_block;\n" +
                "\n" +
                "\telse {\n" +
                "\n" +
                "\t\tfor (i = 0; i < nblocks; i++) {\n" +
                "\n" +
                "\t\t\tgid_t *b;\n" +
                "\n" +
                "\t\t\tb = (void *)__get_free_page(GFP_USER);\n" +
                "\n" +
                "\t\t\tif (!b)\n" +
                "\n" +
                "\t\t\t\tgoto out_undo_partial_alloc;\n" +
                "\n" +
                "\t\t\tgroup_info->blocks[i] = b;\n" +
                "\n" +
                "\t\t}\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\treturn group_info;\n" +
                "\n" +
                "\n" +
                "\n" +
                "out_undo_partial_alloc:\n" +
                "\n" +
                "\twhile (--i >= 0) {\n" +
                "\n" +
                "\t\tfree_page((unsigned long)group_info->blocks[i]);\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\tkfree(group_info);\n" +
                "\n" +
                "\treturn NULL;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "EXPORT_SYMBOL(groups_alloc);\n" +
                "\n" +
                "\n" +
                "\n" +
                "void groups_free(struct group_info *group_info)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tif (group_info->blocks[0] != group_info->small_block) {\n" +
                "\n" +
                "\t\tint i;\n" +
                "\n" +
                "\t\tfor (i = 0; i < group_info->nblocks; i++)\n" +
                "\n" +
                "\t\t\tfree_page((unsigned long)group_info->blocks[i]);\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\tkfree(group_info);\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "EXPORT_SYMBOL(groups_free);\n" +
                "\n" +
                "\n" +
                "\n" +
                "/* export the group_info to a user-space array */\n" +
                "\n" +
                "static int groups_to_user(gid_t __user *grouplist,\n" +
                "\n" +
                "\t\t\t  const struct group_info *group_info)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tint i;\n" +
                "\n" +
                "\tunsigned int count = group_info->ngroups;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tfor (i = 0; i < group_info->nblocks; i++) {\n" +
                "\n" +
                "\t\tunsigned int cp_count = min(NGROUPS_PER_BLOCK, count);\n" +
                "\n" +
                "\t\tunsigned int len = cp_count * sizeof(*grouplist);\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\tif (copy_to_user(grouplist, group_info->blocks[i], len))\n" +
                "\n" +
                "\t\t\treturn -EFAULT;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\tgrouplist += NGROUPS_PER_BLOCK;\n" +
                "\n" +
                "\t\tcount -= cp_count;\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\treturn 0;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "/* fill a group_info from a user-space array - it must be allocated already */\n" +
                "\n" +
                "static int groups_from_user(struct group_info *group_info,\n" +
                "\n" +
                "    gid_t __user *grouplist)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tint i;\n" +
                "\n" +
                "\tunsigned int count = group_info->ngroups;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tfor (i = 0; i < group_info->nblocks; i++) {\n" +
                "\n" +
                "\t\tunsigned int cp_count = min(NGROUPS_PER_BLOCK, count);\n" +
                "\n" +
                "\t\tunsigned int len = cp_count * sizeof(*grouplist);\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\tif (copy_from_user(group_info->blocks[i], grouplist, len))\n" +
                "\n" +
                "\t\t\treturn -EFAULT;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\tgrouplist += NGROUPS_PER_BLOCK;\n" +
                "\n" +
                "\t\tcount -= cp_count;\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\treturn 0;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "/* a simple Shell sort */\n" +
                "\n" +
                "static void groups_sort(struct group_info *group_info)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tint base, max, stride;\n" +
                "\n" +
                "\tint gidsetsize = group_info->ngroups;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tfor (stride = 1; stride < gidsetsize; stride = 3 * stride + 1)\n" +
                "\n" +
                "\t\t; /* nothing */\n" +
                "\n" +
                "\tstride /= 3;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\twhile (stride) {\n" +
                "\n" +
                "\t\tmax = gidsetsize - stride;\n" +
                "\n" +
                "\t\tfor (base = 0; base < max; base++) {\n" +
                "\n" +
                "\t\t\tint left = base;\n" +
                "\n" +
                "\t\t\tint right = left + stride;\n" +
                "\n" +
                "\t\t\tgid_t tmp = GROUP_AT(group_info, right);\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\t\twhile (left >= 0 && GROUP_AT(group_info, left) > tmp) {\n" +
                "\n" +
                "\t\t\t\tGROUP_AT(group_info, right) =\n" +
                "\n" +
                "\t\t\t\t    GROUP_AT(group_info, left);\n" +
                "\n" +
                "\t\t\t\tright = left;\n" +
                "\n" +
                "\t\t\t\tleft -= stride;\n" +
                "\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\tGROUP_AT(group_info, right) = tmp;\n" +
                "\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tstride /= 3;\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "/* a simple bsearch */\n" +
                "\n" +
                "int groups_search(const struct group_info *group_info, gid_t grp)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tunsigned int left, right;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tif (!group_info)\n" +
                "\n" +
                "\t\treturn 0;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tleft = 0;\n" +
                "\n" +
                "\tright = group_info->ngroups;\n" +
                "\n" +
                "\twhile (left < right) {\n" +
                "\n" +
                "\t\tunsigned int mid = left + (right - left)/2;\n" +
                "\n" +
                "\t\tif (grp > GROUP_AT(group_info, mid))\n" +
                "\n" +
                "\t\t\tleft = mid + 1;\n" +
                "\n" +
                "\t\telse if (grp < GROUP_AT(group_info, mid))\n" +
                "\n" +
                "\t\t\tright = mid;\n" +
                "\n" +
                "\t\telse\n" +
                "\n" +
                "\t\t\treturn 1;\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\treturn 0;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "/**\n" +
                "\n" +
                " * set_groups - Change a group subscription in a set of credentials\n" +
                "\n" +
                " * @new: The newly prepared set of credentials to alter\n" +
                "\n" +
                " * @group_info: The group list to install\n" +
                "\n" +
                " *\n" +
                "\n" +
                " * Validate a group subscription and, if valid, insert it into a set\n" +
                "\n" +
                " * of credentials.\n" +
                "\n" +
                " */\n" +
                "\n" +
                "int set_groups(struct cred *new, struct group_info *group_info)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tput_group_info(new->group_info);\n" +
                "\n" +
                "\tgroups_sort(group_info);\n" +
                "\n" +
                "\tget_group_info(group_info);\n" +
                "\n" +
                "\tnew->group_info = group_info;\n" +
                "\n" +
                "\treturn 0;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "EXPORT_SYMBOL(set_groups);\n" +
                "\n" +
                "\n" +
                "\n" +
                "/**\n" +
                "\n" +
                " * set_current_groups - Change current's group subscription\n" +
                "\n" +
                " * @group_info: The group list to impose\n" +
                "\n" +
                " *\n" +
                "\n" +
                " * Validate a group subscription and, if valid, impose it upon current's task\n" +
                "\n" +
                " * security record.\n" +
                "\n" +
                " */\n" +
                "\n" +
                "int set_current_groups(struct group_info *group_info)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tstruct cred *new;\n" +
                "\n" +
                "\tint ret;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tnew = prepare_creds();\n" +
                "\n" +
                "\tif (!new)\n" +
                "\n" +
                "\t\treturn -ENOMEM;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tret = set_groups(new, group_info);\n" +
                "\n" +
                "\tif (ret < 0) {\n" +
                "\n" +
                "\t\tabort_creds(new);\n" +
                "\n" +
                "\t\treturn ret;\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\treturn commit_creds(new);\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "EXPORT_SYMBOL(set_current_groups);\n" +
                "\n" +
                "\n" +
                "\n" +
                "SYSCALL_DEFINE2(getgroups, int, gidsetsize, gid_t __user *, grouplist)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tconst struct cred *cred = current_cred();\n" +
                "\n" +
                "\tint i;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tif (gidsetsize < 0)\n" +
                "\n" +
                "\t\treturn -EINVAL;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t/* no need to grab task_lock here; it cannot change */\n" +
                "\n" +
                "\ti = cred->group_info->ngroups;\n" +
                "\n" +
                "\tif (gidsetsize) {\n" +
                "\n" +
                "\t\tif (i > gidsetsize) {\n" +
                "\n" +
                "\t\t\ti = -EINVAL;\n" +
                "\n" +
                "\t\t\tgoto out;\n" +
                "\n" +
                "\t\t}\n" +
                "\n" +
                "\t\tif (groups_to_user(grouplist, cred->group_info)) {\n" +
                "\n" +
                "\t\t\ti = -EFAULT;\n" +
                "\n" +
                "\t\t\tgoto out;\n" +
                "\n" +
                "\t\t}\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "out:\n" +
                "\n" +
                "\treturn i;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "/*\n" +
                "\n" +
                " *\tSMP: Our groups are copy-on-write. We can set them safely\n" +
                "\n" +
                " *\twithout another task interfering.\n" +
                "\n" +
                " */\n" +
                "\n" +
                "\n" +
                "\n" +
                "SYSCALL_DEFINE2(setgroups, int, gidsetsize, gid_t __user *, grouplist)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tstruct group_info *group_info;\n" +
                "\n" +
                "\tint retval;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tif (!nsown_capable(CAP_SETGID))\n" +
                "\n" +
                "\t\treturn -EPERM;\n" +
                "\n" +
                "\tif ((unsigned)gidsetsize > NGROUPS_MAX)\n" +
                "\n" +
                "\t\treturn -EINVAL;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tgroup_info = groups_alloc(gidsetsize);\n" +
                "\n" +
                "\tif (!group_info)\n" +
                "\n" +
                "\t\treturn -ENOMEM;\n" +
                "\n" +
                "\tretval = groups_from_user(group_info, grouplist);\n" +
                "\n" +
                "\tif (retval) {\n" +
                "\n" +
                "\t\tput_group_info(group_info);\n" +
                "\n" +
                "\t\treturn retval;\n" +
                "\n" +
                "\t}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tretval = set_current_groups(group_info);\n" +
                "\n" +
                "\tput_group_info(group_info);\n" +
                "\n" +
                "\n" +
                "\n" +
                "\treturn retval;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "/*\n" +
                "\n" +
                " * Check whether we're fsgid/egid or in the supplemental group..\n" +
                "\n" +
                " */\n" +
                "\n" +
                "int in_group_p(gid_t grp)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tconst struct cred *cred = current_cred();\n" +
                "\n" +
                "\tint retval = 1;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tif (grp != cred->fsgid)\n" +
                "\n" +
                "\t\tretval = groups_search(cred->group_info, grp);\n" +
                "\n" +
                "\treturn retval;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "EXPORT_SYMBOL(in_group_p);\n" +
                "\n" +
                "\n" +
                "\n" +
                "int in_egroup_p(gid_t grp)\n" +
                "\n" +
                "{\n" +
                "\n" +
                "\tconst struct cred *cred = current_cred();\n" +
                "\n" +
                "\tint retval = 1;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tif (grp != cred->egid)\n" +
                "\n" +
                "\t\tretval = groups_search(cred->group_info, grp);\n" +
                "\n" +
                "\treturn retval;\n" +
                "\n" +
                "}";
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene1 = new Scene(root, 348, 106);
        Stage stage = new Stage();
        stage.setTitle("Access Granted");
        stage.setScene(scene1);
        stage.initStyle(StageStyle.UNDECORATED);
        AtomicInteger count = new AtomicInteger(0);
        textArea.setOnKeyPressed(e->{
            if(e.getCode()==KeyCode.BACK_SPACE){
                if(count.get()==3){
                    stage.show();
                    PauseTransition p = new PauseTransition(Duration.seconds(3));

                    p.setOnFinished(er->{
                        System.exit(0);
                    });
                    p.play();
                    count.set(0);
                }else {
                    count.incrementAndGet();
                }
            }else {
                count.set(0);
            }
        });
        textArea.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                event.consume();
                textArea.appendText(kernel.substring(i.get(), i.get() + 4));
                i.set(i.get() + 4);

            }
        });
        Scene scene = new Scene(textArea, 300,300);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
