package net.guizhanss.rebarmobs;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class RebarMobsLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        // Use Paper's recommended mirror to avoid Maven Central rate limits
        resolver.addRepository(
                new RemoteRepository.Builder(
                                "central",
                                "default",
                                MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR)
                        .build());

        classpathBuilder.addLibrary(resolver);
    }
}
