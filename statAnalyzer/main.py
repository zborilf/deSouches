#!/usr/bin/python3

from matplotlib import pyplot as plt
import pandas as pd
import seaborn as sns
import os


def get_dataframe(dir: str = '../deSouches/logs', filename: str = 'knownCells.csv') -> pd.DataFrame:
    filename = os.path.join(dir, filename)
    df = pd.read_csv(filename)
    return df


def plot_knownCells(df: pd.DataFrame, fig_location: str = None,
                    show_figure: bool = False, print_stdout: bool = False):
    # renaming
    _rename = {
        'step': 'krok',
        'group': 'skupina',
        'cells': 'buňek'
    }
    df.rename(columns=_rename, inplace=True)

    # graphing
    sns.set_theme()
    sns.set_context('paper')
    sns.lineplot(x='krok', y='buňek', data=df, palette='deep').set_title('Objevených buněk')
    plt.tight_layout()

    if print_stdout:
        # print interesting values
        steps = sorted(df['krok'].unique())
        for step in steps:
            cells = df.query('krok==' + str(step))['buňek']
            print('cells->step: ', step, ' max: ', cells.max(), ' min: ', cells.min(), ' avg: ', round(cells.mean(), 2))

    if fig_location:
        if not os.path.exists(os.path.dirname(fig_location)):
            os.makedirs(os.path.dirname(fig_location))
        plt.savefig(fig_location)

    if show_figure:
        plt.show()
        plt.close()


def plot_knownCellsSum(df: pd.DataFrame, fig_location: str = None,
                       show_figure: bool = False, print_stdout: bool = False):
    # renaming
    _rename = {
        'step': 'krok',
        'group': 'skupina',
        'cells': 'buňek'
    }
    df.rename(columns=_rename, inplace=True)
    df = df.groupby(['krok']).sum()
    df = df.reset_index()

    # graphing
    plt.clf()
    sns.set_theme()
    sns.set_context('paper')
    sns.lineplot(x='krok', y='buňek', data=df, palette='deep').set_title('Objevených buněk')
    plt.tight_layout()

    if print_stdout:
        # print interesting values
        steps = sorted(df['krok'].unique())
        for step in steps:
            cells = df.query('krok==' + str(step))['buňek']
            print('cells->step: ', step, ' sum: ', cells.to_string(index=False))

    if fig_location:
        if not os.path.exists(os.path.dirname(fig_location)):
            os.makedirs(os.path.dirname(fig_location))
        plt.savefig(fig_location)

    if show_figure:
        plt.show()
        plt.close()


def plot_usedGroups(df: pd.DataFrame, fig_location: str = None,
                    show_figure: bool = False):
    # renaming
    _rename = {
        'step': 'krok',
        'active groups': 'aktivní',
        'all groups': 'celkové',
    }
    df.rename(columns=_rename, inplace=True)

    # graphing
    plt.clf()
    sns.set_theme()
    sns.set_context('paper')
    df = df.melt('krok', var_name='skupin', value_name='počet').reset_index()
    sns.lineplot(x='krok', y='počet', hue='skupin', data=df, markers=True).set_title('Počet aktivních skupin')
    plt.tight_layout()

    if fig_location:
        if not os.path.exists(os.path.dirname(fig_location)):
            os.makedirs(os.path.dirname(fig_location))
        plt.savefig(fig_location)

    if show_figure:
        plt.show()
        plt.close()


def plot_mapSize(df: pd.DataFrame, fig_location: str = None,
                 show_figure: bool = False, print_stdout: bool = False):
    # renaming
    _rename = {
        'step': 'krok',
        'group': 'skupina',
        'size': 'velikost [buňka^2]',
    }
    df.rename(columns=_rename, inplace=True)

    # graphing
    plt.clf()
    sns.set_theme()
    sns.set_context('paper')
    sns.lineplot(x='krok', y='velikost [buňka^2]', data=df, palette='deep').set_title('Velikost mapy')
    plt.tight_layout()

    if print_stdout:
        # print interesting values
        steps = sorted(df['krok'].unique())
        for step in steps:
            cells = df.query('krok==' + str(step))['velikost [buňka^2]']
            print('size-> step count: ', step, ' max: ', cells.max(), ' min: ', cells.min(), ' avg: ',
                  round(cells.mean(), 2))

    if fig_location:
        if not os.path.exists(os.path.dirname(fig_location)):
            os.makedirs(os.path.dirname(fig_location))
        plt.savefig(fig_location)

    if show_figure:
        plt.show()
        plt.close()


def plot_revisitedCells(df: pd.DataFrame, fig_location: str = None,
                        show_figure: bool = False, print_stdout: bool = False):
    df['ratio'] = df['revisited cell count'] / df['all cell count']
    # renaming
    _rename = {
        'step': 'krok',
        'group': 'skupina',
        'average revisited time from available': 'průměr',
        'revisited cell count': 'počet znovuobjevených',
        'all cell count': 'celkový počet',
        'ratio': 'podíl'
    }
    df.rename(columns=_rename, inplace=True)

    # graphing
    fig, axs = plt.subplots(ncols=3, figsize=(12, 4))
    sns.set_theme()
    sns.set_context('paper')

    sns.lineplot(x='krok', y='počet znovuobjevených', data=df, ax=axs[0]).set_title('Počet znovuobjevených')
    sns.lineplot(x='krok', y='podíl', data=df, ax=axs[1]).set_title('Poměr znovuobjevených')

    # i want zero values in sum so droping here
    df['průměr'] = df['průměr'].astype('float64')
    df.dropna(inplace=True)

    sns.lineplot(x='krok', y='průměr', data=df, ax=axs[2]).set_title('Čas k znovuobjevení')

    if print_stdout:
        # print interesting values
        steps = sorted(df['krok'].unique())
        for step in steps:
            cells = df.query('krok==' + str(step))['počet znovuobjevených']
            print('count-> step count: ', step, ' max: ', cells.max(), ' min: ', cells.min(), ' avg: ',
                  round(cells.mean(), 2))
        for step in steps:
            cells = df.query('krok==' + str(step))['průměr']
            print('avg-> step: ', step, ' max: ', cells.max(), ' min: ', cells.min(), ' avg: ', round(cells.mean(), 2))

    plt.tight_layout()
    if fig_location:
        if not os.path.exists(os.path.dirname(fig_location)):
            os.makedirs(os.path.dirname(fig_location))
        plt.savefig(fig_location)

    if show_figure:
        plt.show()
        plt.close()


def plot_finallCellsSum(df: pd.DataFrame, fig_location: str = None,
                        show_figure: bool = False):
    _rename = {
        'step': 'krok',
        'group': 'skupina',
        'dispenser': 'vydavač',
        'role': 'zóna rolí',
        'task': 'zóna úkolů',
        'goal': 'cílová zóna'
    }

    # renaming

    df.rename(columns=_rename, inplace=True)
    df = df.melt(id_vars=['krok', 'skupina'], var_name='typ', value_name='počet')
    df = df.groupby(['krok', 'typ']).sum()
    df = df.reset_index()

    # graphing
    plt.clf()
    sns.set_theme()
    sns.set_context('paper')
    sns.lineplot(x='krok', y='počet', data=df, palette='deep', hue='typ').set_title(
        'Objevených neměnných míst')
    plt.tight_layout()

    if fig_location:
        if not os.path.exists(os.path.dirname(fig_location)):
            os.makedirs(os.path.dirname(fig_location))
        plt.savefig(fig_location)

    if show_figure:
        plt.show()
        plt.close()


if __name__ == '__main__':
    df = get_dataframe(filename='knownCells.csv')
    plot_knownCells(df=df, fig_location='figures/knownCells.pdf')
    plot_knownCellsSum(df=df, fig_location='figures/knownCellsSum.pdf')

    df = get_dataframe(filename='usedGroups.csv')
    plot_usedGroups(df=df, fig_location='figures/usedGroups.pdf')

    df = get_dataframe(filename='mapSize.csv')
    plot_mapSize(df=df, fig_location='figures/mapSize.pdf')

    df = get_dataframe(filename='revisitedCells.csv')
    plot_revisitedCells(df=df, fig_location='figures/revisitedCells.pdf')

    df = get_dataframe(filename='finalBlocks.csv')
    plot_finallCellsSum(df=df, fig_location='figures/finalBlocks.pdf')
