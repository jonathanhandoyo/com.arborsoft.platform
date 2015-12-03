package com.arborsoft.platform;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application
        return application.banner((environment, sourceClass, out) -> {
            out.println();
            out.println("                                       ,_-=(!7(7/zs_.             ");
            out.println("                                   .='  ' .`/,/!(=)Zm.           ");
            out.println("                     .._,,._..  ,-`- `,\\ ` -` -`\\\\7//WW.         ");
            out.println("                ,v=~/.-,-\\- -!|V-s.)iT-|s|\\-.'   `///mK%.        ");
            out.println("              v!`i!-.e]-g`bT/i(/[=.Z/m)K(YNYi..   /-]i44M.       ");
            out.println("            v`/,`|v]-DvLcfZ/eV/iDLN\\D/ZK@%8W[Z..   `/d!Z8m       ");
            out.println("           //,c\\(2(X/NYNY8]ZZ/bZd\\()/\\7WY%WKKW)   -'|(][%4.      ");
            out.println("         ,\\\\i\\c(e)WX@WKKZKDKWMZ8(b5/ZK8]Z7%ffVM,   -.Y!bNMi      ");
            out.println("         /-iit5N)KWG%%8%%%%W8%ZWM(8YZvD)XN(@.  [   \\]!/GXW[      ");
            out.println("        / ))G8\\NMN%W%%%%%%%%%%8KK@WZKYK*ZG5KMi,-   vi[NZGM[      ");
            out.println("       i\\!(44Y8K%8%%%**~YZYZ@%%%%%4KWZ/PKN)ZDZ7   c=//WZK%!      ");
            out.println("      ,\\v\\YtMZW8W%%f`,`.t/bNZZK%%W%%ZXb*K(K5DZ   -c\\\\/KM48       ");
            out.println("      -|c5PbM4DDW%f  v./c\\[tMY8W%PMW%D@KW)Gbf   -/(=ZZKM8[       ");
            out.println("      2(N8YXWK85@K   -'c|K4/KKK%@  V%@@WD8e~  .//ct)8ZK%8`       ");
            out.println("      =)b%]Nd)@KM[  !'\\cG!iWYK%%|   !M@KZf    -c\\))ZDKW%`        ");
            out.println("      YYKWZGNM4/Pb  '-VscP4]b@W%     'Mf`   -L\\///KM(%W!         ");
            out.println("      !KKW4ZK/W7)Z. '/cttbY)DKW%     -`  .',\\v)K(5KW%%f          ");
            out.println("      'W)KWKZZg)Z2/,!/L(-DYYb54%  ,,`, -\\-/v(((KK5WW%f           ");
            out.println("       \\M4NDDKZZ(e!/\\7vNTtZd)8\\Mi!\\-,-/i-v((tKNGN%W%%            ");
            out.println("       'M8M88(Zd))///((|D\\tDY\\\\KK-`/-i(=)KtNNN@W%%%@%[           ");
            out.println("        !8%@KW5KKN4///s(\\Pd!ROBY8/=2(/4ZdzKD%K%%%M8@%%           ");
            out.println("         '%%%W%dGNtPK(c\\/2\\[Z(ttNYZ2NZW8W8K%%%%YKM%M%%.          ");
            out.println("           *%%W%GW5@/%!e]_tZdY()v)ZXMZW%W%%%*5Y]K%ZK%8[          ");
            out.println("            '*%%%%8%8WK\\)[/ZmZ/Zi]!/M%%%%@f\\ \\Y/NNMK%%!          ");
            out.println("              'VM%%%%W%WN5Z/Gt5/b)((cV@f`  - |cZbMKW%%|          ");
            out.println("                 'V*M%%%WZ/ZG\\t5((+)L\\'-,,/  -)X(NWW%%           ");
            out.println("                      `~`MZ/DZGNZG5(((\\,    ,t\\\\Z)KW%@           ");
            out.println("                         'M8K%8GN8\\5(5///]i!v\\K)85W%%f           ");
            out.println("                           YWWKKKKWZ8G54X/GGMeK@WM8%@            ");
            out.println("                            !M8%8%48WG@KWYbW%WWW%%%@             ");
            out.println("                              VM%WKWK%8K%%8WWWW%%%@`             ");
            out.println("                                ~*%%%%%%W%%%%%%%@~               ");
            out.println("                                   ~*MM%%%%%%@f`                 ");
            out.println("                                       '''''                     ");
        })
                .sources(Application.class);
    }

    public static void main(String[] args) {
        new Application().configure(new SpringApplicationBuilder(Application.class)).run(args);
    }
}
