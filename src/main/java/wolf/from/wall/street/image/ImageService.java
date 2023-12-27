package wolf.from.wall.street.image;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import wolf.from.wall.street.rates.Rate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("classpath:rate.png")
    private Resource resource;
    @Value("${app.rate.image.path}")
    private String path;

    @SneakyThrows
    public void createNewRateImage(List<Rate> rates) {
        final BufferedImage image = ImageIO.read(resource.getInputStream());
        Graphics g = image.getGraphics();
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(Font.BOLD, 50f);
        g.setFont(newFont);
        g.setColor(Color.BLACK);
        Map<Integer, Integer> map = rates.stream().collect(Collectors.toMap(itm -> itm.getResource().getId(), Rate::getPrice));
        g.drawString(map.get(1) + "tg", 80, 630);
        g.drawString(map.get(3) + "tg", 350, 630);
        g.drawString(map.get(2) + "tg", 620, 630);
        g.dispose();
        ImageIO.write(image, "png", new File(path));
    }
}
