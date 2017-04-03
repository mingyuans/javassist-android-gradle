# Javassist-android-gradle
An gradle plugin example that transform class by javassist when build android project. 

## Usage
### Implement transform rule   
1. Implement CustomClassTransformer.java at com/mingyuans/javassist/custom.
2. Register to CustomClassTransformerFactory;  

### Compile 
```
gradle jar
```
### Config build.gradle of the android project 
```java
import com.sun.tools.attach.*;
import java.lang.management.*;
class AgentPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        print("vm name : " + nameOfRunningVM)

        String agentJarPath = "/xxxx/xxxxx.jar" //path of your plugin jar.
        if (!new File(agentJarPath).exists()) {
            print("not exist! agetn jar path : " + agentJarPath)
            return
        }
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            System.setProperty("Mingyuans.AgentArgs","debug=true;")
            vm.loadAgent(agentJarPath, System.getProperty("Mingyuans.AgentArgs"));
            vm.detach();
        } catch (Exception e) {
            print(e.getMessage())
            throw new RuntimeException(e);
        }
    }
}

apply plugin: AgentPlugin

```



