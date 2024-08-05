package edu.school21;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes({"edu.school21.HtmlForm", "edu.school21.HtmlInput"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(HtmlForm.class)) {
            HtmlForm htmlForm = element.getAnnotation(HtmlForm.class);
            String fileName = htmlForm.fileName();
            StringBuilder stringBuilder = new StringBuilder(String.format("<form action = \"%s\" method = \"%s\">\n", htmlForm.action(), htmlForm.method()));
            for (Element innerElement : element.getEnclosedElements()) {
                HtmlInput htmlInput = innerElement.getAnnotation(HtmlInput.class);
                if (htmlInput != null) {
                    stringBuilder.append(String.format("\t\t<input type = \"%s\" name = \"%s\" placeholder = \"%s\">\n",
                            htmlInput.type(), htmlInput.name(), htmlInput.placeholder()));
                }
            }
            stringBuilder.append("<input type = \"submit\" value = \"Send\">\n</form>");
            try {
                FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
                try (Writer writer = file.openWriter()) {
                    writer.write(stringBuilder.toString());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return true;
    }
}
