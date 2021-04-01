package cn.lxw.ss;

import cn.lxw.ss.entity.Course;
import cn.lxw.ss.entity.Dict;
import cn.lxw.ss.entity.User;
import cn.lxw.ss.mapper.CourseMapper;
import cn.lxw.ss.mapper.DictMapper;
import cn.lxw.ss.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：楼兰
 * @date ：Created in 2021/1/4
 * @description:
 **/

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingJDBCTest {
    @Resource
    CourseMapper courseMapper;
    @Resource
    DictMapper dictMapper;
    @Resource
    UserMapper userMapper;

    @Test
    public void addCourse(){
        for(int i = 0 ; i < 10 ; i ++){
            Course c = new Course();
            //c.setCid(Long.valueOf(i));
            c.setCname("shardingsphere");
            c.setUserId(Long.valueOf(""+(1000+i)));
            c.setCstatus("1");
            courseMapper.insert(c);
        }
    }

    @Test
    public void queryCourse(){
        //select * from course
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("cid");
        wrapper.eq("cid",584469194532196353L);
//        wrapper.in()
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(System.out::println);
    }

    @Test
    public void queryOrderRange(){
        //select * from course
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.between("cid",584469193160658944L,584469195731767297L);
//        wrapper.in()
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(System.out::println);
    }

    @Test
    public void queryCourseComplex(){
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.between("cid",584473291188604928L,584473293092818945L);
        wrapper.eq("user_id",1009L);
//        wrapper.in()
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(System.out::println);
    }

    @Test
    public void queryCourseByHint(){
        HintManager hintManager = HintManager.getInstance();
        hintManager.addTableShardingValue("course",2);
        hintManager.addDatabaseShardingValue("course", 1);
        List<Course> courses = courseMapper.selectList(null);
        courses.forEach(System.out::println);
        hintManager.close();
    }

    @Test
    public void addDict(){
        Dict d1 = new Dict();
        d1.setUstatus("1");
        d1.setUvalue("正常");
        dictMapper.insert(d1);

        Dict d2 = new Dict();
        d2.setUstatus("0");
        d2.setUvalue("不正常");
        dictMapper.insert(d2);

        for(int i = 0 ; i < 10 ; i ++){
            User user = new User();
            user.setUsername("user No "+i);
            user.setUstatus(""+(i%2));
            user.setUage(i*10);
            userMapper.insert(user);
        }
    }

    @Test
    public void queryUserStatus(){
        List<User> users = userMapper.queryUserStatus();
        users.forEach(System.out::println);
    }

    @Test
    public void addDictByMS(){
        Dict d1 = new Dict();
        d1.setUstatus("1");
        d1.setUvalue("正常");
        dictMapper.insert(d1);

        Dict d2 = new Dict();
        d2.setUstatus("0");
        d2.setUvalue("不正常");
        dictMapper.insert(d2);
    }

    @Test
    public void queryDictByMS(){
        List<Dict> dicts = dictMapper.selectList(null);
        dicts.forEach(System.out::println);
    }

}
