package top.gregtao.concerto.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import top.gregtao.concerto.music.NeteaseCloudMusic;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class NeteaseLevelArgumentType implements ArgumentType<NeteaseCloudMusic.Level> {

    private NeteaseLevelArgumentType() {
    }

    public static NeteaseLevelArgumentType level() {
        return new NeteaseLevelArgumentType();
    }

    public static NeteaseCloudMusic.Level getOrderType(CommandContext<FabricClientCommandSource> context, String id) {
        try {
            return context.getArgument(id, NeteaseCloudMusic.Level.class);
        } catch (IllegalArgumentException e) {
            return NeteaseCloudMusic.Level.HIRES;
        }
    }

    @Override
    public NeteaseCloudMusic.Level parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        for (NeteaseCloudMusic.Level level : NeteaseCloudMusic.Level.values()) {
            if (level.asString().equals(s)) return level;
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(s);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(NeteaseCloudMusic.Level.values()).map(NeteaseCloudMusic.Level::asString), builder);
    }
}
