package com.wt.complaint.manage.backups;

import com.wt.complaint.manage.bootstrap.ComplaintManageBootstrap;
import com.wt.nr.common.utils.GsonUtil;
import lombok.Setter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * 
 * @author liubin
 *
 */
@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ComplaintManageBootstrap.class})
public class BaseTest {
    @Setter
    private String baseDir;

    @Before
    public void setUp() throws Exception {


    }


    public void testRun() {
        // 空单元测试，避免maven test failed
    }


    public <T> T getTestData(String filename, Class<T> clazz) {
        return GsonUtil.fromJson(getTestData(filename), clazz);
    }


    public String getTestData(String filename) {
        String path;
        if (baseDir != null && !baseDir.isEmpty()) {
            path = String.format("%s/%s", baseDir, filename);
        } else {
            path = filename;
        }
        InputStream is = this.getClass().getResourceAsStream(getRelativePath(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder content = new StringBuilder();
        try {
            String line = br.readLine();
            while (line != null) {
                if (notCommentLine(line)) {
                    content.append(line);
                    content.append("\n");
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return content.toString();
    }


    public <T> Map<Integer, T> getTestIntMapData(String filename, Class<T> clazz) {
        Map<String, Object> map = getTestData(filename, Map.class);
        if (map == null) {
            return null;
        }


        Map<Integer, T> result = new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            result.put(Integer.parseInt(e.getKey()), GsonUtil.fromJson(GsonUtil.toJson(e.getValue()), clazz));
        }
        return result;
    }


    public <T> Map<String, T> getTestStrMapData(String filename, Class<T> clazz) {
        Map<String, Object> map = getTestData(filename, Map.class);
        if (map == null) {
            return null;
        }


        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            result.put(e.getKey(), GsonUtil.fromJson(GsonUtil.toJson(e.getValue()), clazz));
        }
        return result;
    }


    public <T> void assertTypeEquals(String exceptedFilename, T actual, Class<T> clazz) {
        assertEquals(getTestData(exceptedFilename, clazz), actual);
    }


    public <T> void assertTypeEquals(T excepted, T actual, Class<T> clazz) {
        assertEquals(GsonUtil.toJson(excepted), GsonUtil.toJson(actual));
    }


    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);


        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);


        field.set(null, newValue);
    }


    private String getRelativePath(String filename) {
        return String.format("%s/%s", this.getClass().getSimpleName(), filename);
    }


    private boolean notCommentLine(String line) {
        return !line.trim().startsWith("//") && !line.trim().startsWith("#");
    }

}
